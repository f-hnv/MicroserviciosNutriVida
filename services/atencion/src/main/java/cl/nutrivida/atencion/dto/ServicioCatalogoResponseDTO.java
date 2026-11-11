package cl.nutrivida.atencion.dto;

import java.math.BigDecimal;

public record ServicioCatalogoResponseDTO(
        String id,
        String profesionalId,
        String titulo,
        BigDecimal precio,
        BigDecimal descuento,
        String modalidad,
        int duracion,
        boolean activo) {}
