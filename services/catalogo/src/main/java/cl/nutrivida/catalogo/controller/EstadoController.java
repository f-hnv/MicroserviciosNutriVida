package cl.nutrivida.catalogo.controller;

import cl.nutrivida.catalogo.dto.EstadoResponseDTO;
import cl.nutrivida.catalogo.service.EstadoService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EstadoController {
    private final EstadoService estadoService;

    public EstadoController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    @Operation(summary = "Comprobar el servicio y su conexión a MySQL")
    @GetMapping("/api/v1/estado")
    public ResponseEntity<EstadoResponseDTO> estado() {
        return ResponseEntity.ok(
                new EstadoResponseDTO("catalogo", estadoService.conectado(), "BASE_INICIAL"));
    }
}
