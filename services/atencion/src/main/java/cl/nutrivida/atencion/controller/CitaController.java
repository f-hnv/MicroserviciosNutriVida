package cl.nutrivida.atencion.controller;

import cl.nutrivida.atencion.dto.CitaRequestDTO;
import cl.nutrivida.atencion.dto.CitaResponseDTO;
import cl.nutrivida.atencion.dto.EstadoCitaRequestDTO;
import cl.nutrivida.atencion.dto.ReprogramarCitaRequestDTO;
import cl.nutrivida.atencion.service.CitaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
@Tag(name = "Citas")
public class CitaController {
    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    @Operation(summary = "Listar las citas autorizadas para el usuario")
    public ResponseEntity<List<CitaResponseDTO>> listar(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(citaService.listar(jwt));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una cita")
    public ResponseEntity<CitaResponseDTO> ver(
            @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(citaService.ver(id, jwt));
    }

    @GetMapping("/disponibilidad")
    @Operation(summary = "Consultar horarios disponibles")
    public ResponseEntity<List<String>> disponibilidad(
            @RequestParam String servicioId,
            @RequestParam LocalDate fecha,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(citaService.horarios(servicioId, fecha, jwt));
    }

    @PostMapping
    @Operation(summary = "Agendar una cita")
    public ResponseEntity<CitaResponseDTO> crear(
            @Valid @RequestBody CitaRequestDTO request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.reservar(request, jwt));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reprogramar una cita")
    public ResponseEntity<CitaResponseDTO> reprogramar(
            @PathVariable String id,
            @Valid @RequestBody ReprogramarCitaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(citaService.reprogramar(id, request, jwt));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de una cita")
    public ResponseEntity<CitaResponseDTO> estado(
            @PathVariable String id,
            @Valid @RequestBody EstadoCitaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(citaService.estado(id, request, jwt));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una cita")
    public ResponseEntity<Void> cancelar(
            @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        citaService.cancelar(id, jwt);
        return ResponseEntity.noContent().build();
    }
}
