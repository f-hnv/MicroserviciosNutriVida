package cl.nutrivida.catalogo.dto;

import java.math.BigDecimal;

public record ServicioResponseDTO(
        String id,
        String categoria,
        String profesionalId,
        String titulo,
        String descripcion,
        BigDecimal precio,
        BigDecimal descuento,
        String modalidad,
        int duracion,
        String imagen,
        String doctor,
        boolean activo) {}
