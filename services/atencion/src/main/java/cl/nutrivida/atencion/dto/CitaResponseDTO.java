package cl.nutrivida.atencion.dto;

import java.time.LocalDate;

public record CitaResponseDTO(
        String id,
        String pacienteId,
        String profesionalId,
        String pacienteNombre,
        String doctor,
        String servicioId,
        String servicioNombre,
        LocalDate fecha,
        String hora,
        int duracion,
        java.math.BigDecimal precio,
        String modalidad,
        String estado,
        String motivo) {}
