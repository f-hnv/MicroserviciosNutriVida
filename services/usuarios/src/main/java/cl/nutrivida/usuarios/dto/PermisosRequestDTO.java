package cl.nutrivida.usuarios.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PermisosRequestDTO(
        @Pattern(regexp = "PACIENTE|MEDICO|ADMIN|VENDEDOR") @NotNull String rol, @NotNull Boolean activo) {}
