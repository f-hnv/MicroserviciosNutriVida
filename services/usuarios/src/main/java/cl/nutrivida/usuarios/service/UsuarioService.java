package cl.nutrivida.usuarios.service;

import cl.nutrivida.usuarios.dto.PerfilRequestDTO;
import cl.nutrivida.usuarios.dto.PermisosRequestDTO;
import cl.nutrivida.usuarios.dto.ProfesionalResponseDTO;
import cl.nutrivida.usuarios.dto.UsuarioRequestDTO;
import cl.nutrivida.usuarios.dto.UsuarioResponseDTO;
import cl.nutrivida.usuarios.dto.UsuarioResumenResponseDTO;
import cl.nutrivida.usuarios.model.Usuario;
import cl.nutrivida.usuarios.repository.SesionRepository;
import cl.nutrivida.usuarios.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final SesionRepository sesionRepository;
    private final AuthService authService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            SesionRepository sesionRepository,
            AuthService authService) {
        this.usuarioRepository = usuarioRepository;
        this.sesionRepository = sesionRepository;
        this.authService = authService;
    }

    public List<ProfesionalResponseDTO> profesionales() {
        return usuarioRepository.findAll().stream()
                .filter(usuario ->
                        Boolean.TRUE.equals(usuario.getActivo())
                                && "MEDICO".equals(usuario.getRol()))
                .map(usuario ->
                        new ProfesionalResponseDTO(
                                usuario.getIdUsuario(),
                                usuario.getNombres() + " " + usuario.getApellidos()))
                .toList();
    }

    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponseDTO::de).toList();
    }

    public UsuarioResumenResponseDTO resumen(String id, Jwt jwt) {
        Usuario usuario = obtener(id);
        if (!id.equals(jwt.getSubject())
                && "PACIENTE".equals(jwt.getClaimAsString("rol"))
                && !"MEDICO".equals(usuario.getRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return new UsuarioResumenResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombres() + " " + usuario.getApellidos(),
                usuario.getRol(),
                Boolean.TRUE.equals(usuario.getActivo()));
    }

    public UsuarioResponseDTO ver(String id, Jwt jwt) {
        validarPropietario(id, jwt);
        return UsuarioResponseDTO.de(obtener(id));
    }

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO request) {
        UsuarioResponseDTO registrado = authService.registrar(request.datos());
        Usuario usuario = obtener(registrado.id());
        usuario.setRol(request.rol());
        return UsuarioResponseDTO.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO editar(String id, PerfilRequestDTO request, Jwt jwt) {
        validarPropietario(id, jwt);
        Usuario usuario = obtener(id);
        usuario.setNombres(request.nombres().strip());
        usuario.setApellidos(request.apellidos().strip());
        usuario.setTelefono(request.telefono());
        usuario.setDireccion(request.direccion().strip());
        usuario.setRegion(request.region());
        usuario.setComuna(request.comuna());
        return UsuarioResponseDTO.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO permisos(String id, PermisosRequestDTO request, Jwt jwt) {
        if (id.equals(jwt.getSubject())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No puedes cambiar tus propios permisos");
        }
        usuarioRepository.bloquearAdministradores();
        Usuario usuario = obtener(id);
        boolean quitarAdministrador = !request.activo() || !"ADMIN".equals(request.rol());
        if ("ADMIN".equals(usuario.getRol())
                && quitarAdministrador
                && usuarioRepository.countByRolAndActivoTrue("ADMIN") <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Debe quedar un administrador activo");
        }
        usuario.setRol(request.rol());
        usuario.setActivo(request.activo());
        sesionRepository.deleteByUsuario(id);
        return UsuarioResponseDTO.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public void desactivar(String id, Jwt jwt) {
        permisos(id, new PermisosRequestDTO(obtener(id).getRol(), false), jwt);
    }

    private Usuario obtener(String id) {
        return usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private void validarPropietario(String id, Jwt jwt) {
        if (!id.equals(jwt.getSubject()) && !"ADMIN".equals(jwt.getClaimAsString("rol"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
