package cl.nutrivida.atencion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EstadoCitaRequestDTO(
        @NotBlank @Pattern(regexp = "PENDIENTE|CONFIRMADA|COMPLETADA|CANCELADA") String estado) {}
