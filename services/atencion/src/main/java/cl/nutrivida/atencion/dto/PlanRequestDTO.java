package cl.nutrivida.atencion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlanRequestDTO(
        @NotBlank String pacienteId,
        String profesionalId,
        @NotBlank @Size(max = 150) String titulo,
        @NotBlank @Size(max = 10000) String contenido) {}
