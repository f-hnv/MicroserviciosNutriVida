package cl.nutrivida.catalogo.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServicioRequestDTO(
        @NotBlank String idCategoria,
        String profesionalId,
        @NotBlank @Size(max = 150) String titulo,
        @NotBlank @Size(max = 2000) String descripcion,
        @NotNull @DecimalMin("0") @Digits(integer = 10, fraction = 0) BigDecimal precio,
        @NotNull @DecimalMin("0") @DecimalMax("90") @Digits(integer = 2, fraction = 2)
                BigDecimal descuento,
        @NotBlank @Pattern(regexp = "PRESENCIAL|ONLINE") String modalidad,
        @Min(1) @Max(600) int duracion,
        @Size(max = 1000) String imagen,
        @NotNull Boolean activo) {}
