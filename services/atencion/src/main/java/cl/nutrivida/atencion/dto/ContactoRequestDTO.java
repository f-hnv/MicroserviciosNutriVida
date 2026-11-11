package cl.nutrivida.atencion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactoRequestDTO(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 254) String correo,
        @NotBlank @Size(max = 500) String comentario) {}
