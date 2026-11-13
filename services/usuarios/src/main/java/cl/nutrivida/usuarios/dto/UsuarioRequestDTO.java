package cl.nutrivida.usuarios.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UsuarioRequestDTO(
        @NotNull @Valid RegistroRequestDTO datos,
        @NotNull @Pattern(regexp = "PACIENTE|MEDICO|ADMIN|VENDEDOR") String rol) {}
