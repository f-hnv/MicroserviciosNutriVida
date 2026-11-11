package cl.nutrivida.ventas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record DetallePedidoRequestDTO(@NotBlank String id, @Min(1) @Max(10) int cantidad) {}
