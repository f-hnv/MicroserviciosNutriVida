package cl.nutrivida.atencion.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProgresoRequestDTO(
        @NotNull LocalDate fecha,
        @NotNull @DecimalMin("20") @DecimalMax("300") @Digits(integer = 3, fraction = 2)
                BigDecimal peso) {}
