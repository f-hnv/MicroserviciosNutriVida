package cl.nutrivida.usuarios.service;

import cl.nutrivida.usuarios.dto.AccesoResponseDTO;
import cl.nutrivida.usuarios.dto.LoginRequestDTO;
import cl.nutrivida.usuarios.dto.RegistroRequestDTO;
import cl.nutrivida.usuarios.dto.ResultadoAutenticacionDTO;
import cl.nutrivida.usuarios.dto.UsuarioResponseDTO;
import cl.nutrivida.usuarios.model.SesionRefresh;
import cl.nutrivida.usuarios.model.Usuario;
import cl.nutrivida.usuarios.repository.SesionRepository;
import cl.nutrivida.usuarios.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.*;
import java.util.*;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final SesionRepository sesionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final SecureRandom random = new SecureRandom();

    public AuthService(
            UsuarioRepository usuarioRepository,
            SesionRepository sesionRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.sesionRepository = sesionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    private ResponseStatusException invalid() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Sesión o credenciales no válidas");
    }

    @Transactional
    public UsuarioResponseDTO registrar(RegistroRequestDTO data) {
        if (data.password().getBytes(StandardCharsets.UTF_8).length > 72)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña supera el máximo permitido en bytes UTF-8");
        String correo = data.correo().strip().toLowerCase(Locale.ROOT),
                run = data.run().toUpperCase(Locale.ROOT);
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)
                || usuarioRepository.existsByRun(run))
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El correo o RUN ya está registrado");
        Usuario u = new Usuario();
        u.setNombres(data.nombres().strip());
        u.setApellidos(data.apellidos().strip());
        u.setCorreo(correo);
        u.setPasswordHash(passwordEncoder.encode(data.password()));
        u.setRun(run);
        u.setTelefono(data.telefono());
        u.setDireccion(data.direccion().strip());
        u.setRegion(data.region());
        u.setComuna(data.comuna());
        u.setRol("PACIENTE");
        u.setActivo(true);
        return UsuarioResponseDTO.de(usuarioRepository.saveAndFlush(u));
    }

    @Transactional
    public ResultadoAutenticacionDTO login(LoginRequestDTO data) {
        if (data.password().getBytes(StandardCharsets.UTF_8).length > 72) throw invalid();
        Usuario u =
                usuarioRepository
                        .findByCorreoIgnoreCase(data.correo().strip())
                        .orElseThrow(this::invalid);
        if (!Boolean.TRUE.equals(u.getActivo())
                || !passwordEncoder.matches(data.password(), u.getPasswordHash())) throw invalid();
        SesionRefresh session = new SesionRefresh();
        session.setId(UUID.randomUUID().toString());
        session.setUsuario(u.getIdUsuario());
        session.setVenceEn(LocalDateTime.now(ZoneOffset.UTC).plusDays(7));
        return renovar(u, session);
    }

    @Transactional
    public ResultadoAutenticacionDTO refresh(String token) {
        SesionRefresh session =
                sesionRepository.findByTokenHash(hash(token)).orElseThrow(this::invalid);
        if (!session.getVenceEn().isAfter(LocalDateTime.now(ZoneOffset.UTC))) throw invalid();
        Usuario u =
                usuarioRepository
                        .findById(session.getUsuario())
                        .filter(x -> Boolean.TRUE.equals(x.getActivo()))
                        .orElseThrow(this::invalid);
        return renovar(u, session);
    }

    @Transactional
    public void logout(String token) {
        sesionRepository.findByTokenHash(hash(token)).ifPresent(sesionRepository::delete);
    }

    private ResultadoAutenticacionDTO renovar(Usuario u, SesionRefresh session) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String refresh = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setTokenHash(hash(refresh));
        sesionRepository.saveAndFlush(session);
        Instant now = Instant.now();
        var claims =
                JwtClaimsSet.builder()
                        .issuer("nutrivida")
                        .audience(List.of("nutrivida-web"))
                        .subject(u.getIdUsuario())
                        .issuedAt(now)
                        .expiresAt(now.plusSeconds(600))
                        .claim("rol", u.getRol())
                        .claim("sid", session.getId())
                        .build();
        String access = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new ResultadoAutenticacionDTO(
                new AccesoResponseDTO(access, UsuarioResponseDTO.de(u)), refresh);
    }

    public static String hash(String value) {
        if (value == null || value.length() > 200)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no válida");
        try {
            return HexFormat.of()
                    .formatHex(
                            MessageDigest.getInstance("SHA-256")
                                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
