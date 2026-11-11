package cl.nutrivida.atencion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CitaRequestDTO(
        @NotBlank String servicioId,
        @NotNull LocalDate fecha,
        @NotBlank @Pattern(regexp = "[0-2][0-9]:[0-5][0-9]") String hora,
        @Size(max = 500) String motivo) {}
