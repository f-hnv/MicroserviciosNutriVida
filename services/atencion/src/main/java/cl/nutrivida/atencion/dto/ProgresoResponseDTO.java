package cl.nutrivida.atencion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProgresoResponseDTO(String id, LocalDate fecha, BigDecimal peso) {}
