package cl.nutrivida.ventas.dto;

import java.math.BigDecimal;
import java.util.List;

public record PedidoResponseDTO(
        String id,
        String usuarioId,
        String fecha,
        String correo,
        String nombre,
        String telefono,
        String direccion,
        String region,
        String comuna,
        String entrega,
        String estado,
        String metodo,
        List<DetallePedidoResponseDTO> items,
        BigDecimal total) {}
