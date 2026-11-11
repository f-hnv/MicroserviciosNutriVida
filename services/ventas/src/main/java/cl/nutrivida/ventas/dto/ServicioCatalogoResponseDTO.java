package cl.nutrivida.ventas.dto;

import java.math.BigDecimal;

public record ServicioCatalogoResponseDTO(
        String id, String titulo, BigDecimal precio, BigDecimal descuento, boolean activo) {}
