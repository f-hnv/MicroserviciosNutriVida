package cl.nutrivida.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PedidoRequestDTO(
        @NotEmpty @Size(max = 20) List<@Valid DetallePedidoRequestDTO> items,
        @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Email @Size(max = 254) String correo,
        @NotBlank @Pattern(regexp = "[0-9]{9}") String telefono,
        @NotBlank @Size(max = 250) String direccion,
        @NotBlank @Size(max = 80) String region,
        @NotBlank @Size(max = 80) String comuna,
        @Pattern(regexp = "PRESENCIAL|ONLINE") @NotNull String entrega,
        @Pattern(regexp = "APROBADO|RECHAZADO") @NotNull String resultado) {}
