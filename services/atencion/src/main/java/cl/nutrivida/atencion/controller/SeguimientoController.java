package cl.nutrivida.atencion.controller;

import cl.nutrivida.atencion.dto.PacienteResponseDTO;
import cl.nutrivida.atencion.dto.PlanRequestDTO;
import cl.nutrivida.atencion.dto.PlanResponseDTO;
import cl.nutrivida.atencion.dto.ProgresoRequestDTO;
import cl.nutrivida.atencion.dto.ProgresoResponseDTO;
import cl.nutrivida.atencion.service.SeguimientoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Planes y progreso")
public class SeguimientoController {
    private final SeguimientoService seguimientoService;

    public SeguimientoController(SeguimientoService seguimientoService) {
        this.seguimientoService = seguimientoService;
    }

    @GetMapping("/pacientes")
    @Operation(summary = "Listar pacientes de la agenda clínica")
    public ResponseEntity<List<PacienteResponseDTO>> pacientes(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(seguimientoService.pacientes(jwt));
    }

    @GetMapping("/planes")
    @Operation(summary = "Consultar planes alimenticios")
    public ResponseEntity<List<PlanResponseDTO>> planes(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(seguimientoService.planes(jwt));
    }

    @PostMapping("/planes")
    @Operation(summary = "Asignar un plan alimenticio")
    public ResponseEntity<PlanResponseDTO> asignar(
            @Valid @RequestBody PlanRequestDTO request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seguimientoService.asignar(request, jwt));
    }

    @DeleteMapping("/planes/{id}")
    @Operation(summary = "Desactivar un plan alimenticio")
    public ResponseEntity<Void> eliminarPlan(
            @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        seguimientoService.eliminarPlan(id, jwt);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/progreso")
    @Operation(summary = "Consultar el progreso personal")
    public ResponseEntity<List<ProgresoResponseDTO>> progreso(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(seguimientoService.progreso(jwt));
    }

    @PostMapping("/progreso")
    @Operation(summary = "Registrar el peso del paciente")
    public ResponseEntity<ProgresoResponseDTO> guardar(
            @Valid @RequestBody ProgresoRequestDTO request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seguimientoService.guardarProgreso(request, jwt));
    }

    @DeleteMapping("/progreso/{id}")
    @Operation(summary = "Eliminar un registro de progreso")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        seguimientoService.eliminarProgreso(id, jwt);
        return ResponseEntity.noContent().build();
    }
}
