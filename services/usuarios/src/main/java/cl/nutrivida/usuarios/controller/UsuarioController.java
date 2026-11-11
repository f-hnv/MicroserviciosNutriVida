package cl.nutrivida.usuarios.controller;

import cl.nutrivida.usuarios.dto.PerfilRequestDTO;
import cl.nutrivida.usuarios.dto.PermisosRequestDTO;
import cl.nutrivida.usuarios.dto.ProfesionalResponseDTO;
import cl.nutrivida.usuarios.dto.UsuarioRequestDTO;
import cl.nutrivida.usuarios.dto.UsuarioResponseDTO;
import cl.nutrivida.usuarios.dto.UsuarioResumenResponseDTO;
import cl.nutrivida.usuarios.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Usuarios y profesionales")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/profesionales")
    @Operation(summary = "Listar profesionales activos")
    public ResponseEntity<List<ProfesionalResponseDTO>> profesionales() {
        return ResponseEntity.ok(usuarioService.profesionales());
    }

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios")
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/usuarios/{id}/resumen")
    @Operation(summary = "Consultar el resumen de un usuario")
    public ResponseEntity<UsuarioResumenResponseDTO> resumen(
            @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.resumen(id, jwt));
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Consultar un usuario")
    public ResponseEntity<UsuarioResponseDTO> ver(
            @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.ver(id, jwt));
    }

    @PostMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un usuario y asignar su rol")
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Actualizar los datos de un usuario")
    public ResponseEntity<UsuarioResponseDTO> editar(
            @PathVariable String id,
            @Valid @RequestBody PerfilRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.editar(id, request, jwt));
    }

    @PutMapping("/usuarios/{id}/permisos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar el rol o estado de un usuario")
    public ResponseEntity<UsuarioResponseDTO> permisos(
            @PathVariable String id,
            @Valid @RequestBody PermisosRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.permisos(id, request, jwt));
    }

    @DeleteMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar un usuario")
    public ResponseEntity<Void> desactivar(
            @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        usuarioService.desactivar(id, jwt);
        return ResponseEntity.noContent().build();
    }
}
