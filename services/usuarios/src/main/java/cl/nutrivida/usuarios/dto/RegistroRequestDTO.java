package cl.nutrivida.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequestDTO(
        @NotBlank @Size(max = 100) String nombres,
        @NotBlank @Size(max = 100) String apellidos,
        @NotBlank @Email @Size(max = 254) String correo,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Pattern(regexp = "[0-9]{7,8}-[0-9kK]") String run,
        @NotBlank @Pattern(regexp = "[0-9]{9}") String telefono,
        @NotBlank @Size(max = 250) String direccion,
        @NotBlank @Size(max = 80) String region,
        @NotBlank @Size(max = 80) String comuna) {}
