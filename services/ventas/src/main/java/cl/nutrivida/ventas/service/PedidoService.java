package cl.nutrivida.ventas.service;

import cl.nutrivida.ventas.client.CatalogoClient;
import cl.nutrivida.ventas.dto.DetallePedidoRequestDTO;
import cl.nutrivida.ventas.dto.DetallePedidoResponseDTO;
import cl.nutrivida.ventas.dto.PedidoRequestDTO;
import cl.nutrivida.ventas.dto.PedidoResponseDTO;
import cl.nutrivida.ventas.dto.ServicioCatalogoResponseDTO;
import cl.nutrivida.ventas.model.DetallePedido;
import cl.nutrivida.ventas.model.Pedido;
import cl.nutrivida.ventas.repository.DetallePedidoRepository;
import cl.nutrivida.ventas.repository.PedidoRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.*;

import org.springframework.http.*;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.*;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.sql.*;
import java.time.*;
import java.util.*;

import javax.sql.DataSource;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final CatalogoClient catalogoClient;
    private final DataSource source;
    private final ObjectMapper mapper;

    public PedidoService(
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            DataSource source,
            ObjectMapper mapper,
            CatalogoClient catalogoClient) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.source = source;
        this.mapper = mapper;
        this.catalogoClient = catalogoClient;
    }

    private boolean admin(Jwt jwt) {
        return jwt != null && "ADMIN".equals(jwt.getClaimAsString("rol"));
    }

    // El vendedor solo consulta: ve todas las órdenes, pero no puede crear compras.
    private boolean vendedor(Jwt jwt) {
        return jwt != null && "VENDEDOR".equals(jwt.getClaimAsString("rol"));
    }

    @Transactional
    public PedidoResponseDTO crear(PedidoRequestDTO data, String key, String guestKey, Jwt jwt) {
        if (vendedor(jwt))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El rol vendedor solo puede consultar órdenes");
        if (key == null || !key.matches("[A-Za-z0-9_-]{16,64}"))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Falta una clave de idempotencia válida");
        if (jwt == null && (guestKey == null || guestKey.length() < 32 || guestKey.length() > 128))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Falta la clave de acceso de la compra invitada");
        String requestHash;
        try {
            requestHash = hash(mapper.writeValueAsString(data));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        bloquear(key);
        var existing = pedidoRepository.findByClaveIdempotencia(key);
        if (existing.isPresent()) {
            var previous = existing.get();
            autorizar(previous, guestKey, jwt);
            if (!requestHash.equals(previous.getSolicitudHash()))
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "La clave ya se usó para una compra diferente");
            return vista(previous);
        }
        if (data.items().stream().map(DetallePedidoRequestDTO::id).distinct().count()
                != data.items().size())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Agrupa las unidades del mismo servicio");
        List<DetallePedido> rows = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (var line : data.items()) {
            ServicioCatalogoResponseDTO service = catalogoClient.obtenerServicio(line.id());
            if (service == null || !service.activo())
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Servicio no disponible");
            var unit =
                    service.precio()
                            .multiply(BigDecimal.ONE.subtract(service.descuento().movePointLeft(2)))
                            .setScale(0, RoundingMode.HALF_UP);
            var row = new DetallePedido();
            row.setIdServicio(service.id());
            row.setTituloServicio(service.titulo());
            row.setCantidad((short) line.cantidad());
            row.setPrecioBase(service.precio());
            row.setDescuentoPorcentaje(service.descuento());
            row.setPrecioUnitario(unit);
            rows.add(row);
            total = total.add(unit.multiply(BigDecimal.valueOf(line.cantidad())));
        }
        var p = new Pedido();
        p.setIdUsuario(jwt == null ? null : jwt.getSubject());
        p.setGuestTokenHash(jwt == null ? hash(guestKey) : null);
        p.setSolicitudHash(requestHash);
        p.setClaveIdempotencia(key);
        p.setNombreCliente(data.nombre().strip());
        p.setCorreoCliente(data.correo().strip().toLowerCase());
        p.setTelefonoCliente(data.telefono());
        p.setDireccionCliente(data.direccion().strip());
        p.setRegionCliente(data.region());
        p.setComunaCliente(data.comuna());
        p.setModalidad(data.entrega());
        p.setTotal(total);
        p.setEstadoPago("APROBADO".equals(data.resultado()) ? "PAGADA" : "RECHAZADA");
        p.setMetodoPago("PAGO_SIMULADO");
        pedidoRepository.saveAndFlush(p);
        for (var row : rows) {
            row.setIdPedido(p.getIdPedido());
            detallePedidoRepository.save(row);
        }
        detallePedidoRepository.flush();
        return vista(p);
    }

    public List<PedidoResponseDTO> listar(Jwt jwt) {
        return pedidoRepository.findAll().stream()
                .filter(p ->
                        admin(jwt)
                                || vendedor(jwt)
                                || Objects.equals(p.getIdUsuario(), jwt.getSubject()))
                .sorted(Comparator.comparing(Pedido::getFechaCreacion).reversed())
                .map(this::vista)
                .toList();
    }

    public PedidoResponseDTO ver(String id, String guestKey, Jwt jwt) {
        var p =
                pedidoRepository
                        .findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!vendedor(jwt)) autorizar(p, guestKey, jwt);
        return vista(p);
    }

    private void autorizar(Pedido p, String guestKey, Jwt jwt) {
        if (admin(jwt) || (jwt != null && Objects.equals(p.getIdUsuario(), jwt.getSubject())))
            return;
        if (p.getIdUsuario() == null
                && p.getGuestTokenHash() != null
                && guestKey != null
                && guestKey.length() <= 128
                && MessageDigest.isEqual(
                        p.getGuestTokenHash().getBytes(StandardCharsets.US_ASCII),
                        hash(guestKey).getBytes(StandardCharsets.US_ASCII))) return;
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a esta compra");
    }

    private PedidoResponseDTO vista(Pedido p) {
        return new PedidoResponseDTO(
                p.getIdPedido(),
                p.getIdUsuario(),
                p.getFechaCreacion().atOffset(ZoneOffset.UTC).toString(),
                p.getCorreoCliente(),
                p.getNombreCliente(),
                p.getTelefonoCliente(),
                p.getDireccionCliente(),
                p.getRegionCliente(),
                p.getComunaCliente(),
                p.getModalidad(),
                p.getEstadoPago(),
                "Pago simulado",
                detallePedidoRepository.findByIdPedido(p.getIdPedido()).stream()
                        .map(d ->
                                new DetallePedidoResponseDTO(
                                        d.getIdServicio(),
                                        d.getTituloServicio(),
                                        d.getCantidad(),
                                        d.getPrecioUnitario()))
                        .toList(),
                p.getTotal());
    }

    private static String hash(String value) {
        try {
            return HexFormat.of()
                    .formatHex(
                            MessageDigest.getInstance("SHA-256")
                                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private void bloquear(String key) {
        Connection connection = DataSourceUtils.getConnection(source);
        String name = "pedido-" + hash(key).substring(0, 48);
        try (var stmt = connection.prepareStatement("SELECT GET_LOCK(?,3)")) {
            stmt.setString(1, name);
            try (var rs = stmt.executeQuery()) {
                if (!rs.next() || rs.getInt(1) != 1)
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "La compra ya se está procesando");
            }
        } catch (SQLException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "No se pudo procesar la compra");
        }
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        try (var stmt = connection.prepareStatement("SELECT RELEASE_LOCK(?)")) {
                            stmt.setString(1, name);
                            stmt.execute();
                        } catch (SQLException e) {
                            throw new IllegalStateException(
                                    "No se pudo liberar el bloqueo de compra", e);
                        }
                    }
                });
    }
}
