package cl.nutrivida.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PerfilRequestDTO(
        @NotBlank @Size(max = 100) String nombres,
        @NotBlank @Size(max = 100) String apellidos,
        @NotBlank @Pattern(regexp = "[0-9]{9}") String telefono,
        @NotBlank @Size(max = 250) String direccion,
        @NotBlank @Size(max = 80) String region,
        @NotBlank @Size(max = 80) String comuna) {}
