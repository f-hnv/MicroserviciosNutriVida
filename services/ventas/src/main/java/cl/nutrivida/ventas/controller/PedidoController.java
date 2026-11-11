package cl.nutrivida.ventas.controller;

import cl.nutrivida.ventas.dto.PedidoRequestDTO;
import cl.nutrivida.ventas.dto.PedidoResponseDTO;
import cl.nutrivida.ventas.service.PedidoService;

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
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    @Operation(summary = "Listar pedidos autorizados para el usuario")
    public ResponseEntity<List<PedidoResponseDTO>> listar(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(pedidoService.listar(jwt));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un pedido")
    public ResponseEntity<PedidoResponseDTO> ver(
            @PathVariable String id,
            @RequestHeader(name = "X-Pedido-Key", required = false) String guestKey,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(pedidoService.ver(id, guestKey, jwt));
    }

    @PostMapping
    @Operation(summary = "Crear un pedido con pago simulado")
    public ResponseEntity<PedidoResponseDTO> crear(
            @Valid @RequestBody PedidoRequestDTO request,
            @RequestHeader(name = "Idempotency-Key") String key,
            @RequestHeader(name = "X-Pedido-Key", required = false) String guestKey,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crear(request, key, guestKey, jwt));
    }
}
