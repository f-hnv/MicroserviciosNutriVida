package cl.nutrivida.usuarios.controller;

import cl.nutrivida.usuarios.dto.AccesoResponseDTO;
import cl.nutrivida.usuarios.dto.LoginRequestDTO;
import cl.nutrivida.usuarios.dto.RegistroRequestDTO;
import cl.nutrivida.usuarios.dto.ResultadoAutenticacionDTO;
import cl.nutrivida.usuarios.dto.UsuarioResponseDTO;
import cl.nutrivida.usuarios.service.AuthService;
import cl.nutrivida.usuarios.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación")
public class AuthController {
    private final AuthService auth;
    private final UsuarioService usuarioService;
    private final boolean secure;
    private final Set<String> origins;

    public AuthController(
            AuthService auth,
            UsuarioService usuarioService,
            @Value("${app.cookie-secure:false}") boolean secure,
            @Value("${app.frontend-origins}") String origins) {
        this.auth = auth;
        this.usuarioService = usuarioService;
        this.secure = secure;
        this.origins = Set.of(origins.split(","));
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar una cuenta de paciente")
    public ResponseEntity<UsuarioResponseDTO> registro(
            @Valid @RequestBody RegistroRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auth.registrar(data));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<AccesoResponseDTO> login(@Valid @RequestBody LoginRequestDTO data) {
        return acceso(auth.login(data));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar la sesión")
    public ResponseEntity<AccesoResponseDTO> refresh(
            @CookieValue(name = "nutrivida_refresh", required = false) String token,
            @RequestHeader(name = "X-NutriVida-Client") String client,
            HttpServletRequest request) {
        check(client, request);
        return acceso(auth.refresh(token));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión y revocar su acceso")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "nutrivida_refresh", required = false) String token,
            @RequestHeader(name = "X-NutriVida-Client") String client,
            HttpServletRequest request) {
        check(client, request);
        if (token != null) auth.logout(token);
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie("", 0)).build();
    }

    @GetMapping("/me")
    @Operation(summary = "Consultar el perfil de la sesión actual")
    public ResponseEntity<UsuarioResponseDTO> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.ver(jwt.getSubject(), jwt));
    }

    private ResponseEntity<AccesoResponseDTO> acceso(ResultadoAutenticacionDTO result) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie(result.refresh(), 7 * 24 * 3600))
                .body(result.acceso());
    }

    private String cookie(String value, long age) {
        return ResponseCookie.from("nutrivida_refresh", value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(age)
                .build()
                .toString();
    }

    private void check(String client, HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (!"web".equals(client) || (origin != null && !origins.contains(origin)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Origen no permitido");
    }
}
