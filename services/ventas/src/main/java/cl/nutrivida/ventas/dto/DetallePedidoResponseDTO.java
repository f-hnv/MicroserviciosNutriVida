package cl.nutrivida.ventas.dto;

import java.math.BigDecimal;

public record DetallePedidoResponseDTO(String id, String titulo, int cantidad, BigDecimal precio) {}
