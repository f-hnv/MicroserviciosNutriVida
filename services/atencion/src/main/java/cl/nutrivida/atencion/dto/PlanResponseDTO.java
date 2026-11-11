package cl.nutrivida.atencion.dto;

import java.time.LocalDate;

public record PlanResponseDTO(
        String id,
        String pacienteId,
        String titulo,
        String contenido,
        String profesional,
        LocalDate fecha,
        boolean activo) {}
