package cl.nutrivida.ventas.model;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
public class Pedido {
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "guest_token_hash", columnDefinition = "char(64)")
    private String guestTokenHash;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "solicitud_hash", columnDefinition = "char(64)")
    private String solicitudHash;

    public String getGuestTokenHash() {
        return guestTokenHash;
    }

    public void setGuestTokenHash(String value) {
        guestTokenHash = value;
    }

    public String getSolicitudHash() {
        return solicitudHash;
    }

    public void setSolicitudHash(String value) {
        solicitudHash = value;
    }

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_pedido", nullable = false, columnDefinition = "char(36)")
    private String idPedido;

    @Column(name = "clave_idempotencia", nullable = false, columnDefinition = "varchar(64)")
    private String claveIdempotencia;

    @Column(name = "nombre_cliente", nullable = false, columnDefinition = "varchar(120)")
    private String nombreCliente;

    @Column(name = "correo_cliente", nullable = false, columnDefinition = "varchar(254)")
    private String correoCliente;

    @Column(name = "telefono_cliente", nullable = false, columnDefinition = "varchar(20)")
    private String telefonoCliente;

    @Column(name = "direccion_cliente", nullable = false, columnDefinition = "varchar(250)")
    private String direccionCliente;

    @Column(name = "region_cliente", nullable = false, columnDefinition = "varchar(80)")
    private String regionCliente;

    @Column(name = "comuna_cliente", nullable = false, columnDefinition = "varchar(80)")
    private String comunaCliente;

    @Column(name = "modalidad", nullable = false, columnDefinition = "varchar(16)")
    private String modalidad;

    @Column(name = "total", nullable = false, columnDefinition = "decimal(12,2)")
    private BigDecimal total;

    @Column(name = "estado_pago", nullable = false, columnDefinition = "varchar(16)")
    private String estadoPago;

    @Column(name = "metodo_pago", nullable = false, columnDefinition = "varchar(32)")
    private String metodoPago;

    @Column(name = "fecha_creacion", nullable = false, columnDefinition = "datetime(6)")
    @CreationTimestamp(source = org.hibernate.annotations.SourceType.DB)
    private LocalDateTime fechaCreacion;

    @Column(name = "id_usuario", nullable = true, columnDefinition = "char(36)")
    private String idUsuario;

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String value) {
        this.idPedido = value;
    }

    public String getClaveIdempotencia() {
        return claveIdempotencia;
    }

    public void setClaveIdempotencia(String value) {
        this.claveIdempotencia = value;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String value) {
        this.nombreCliente = value;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String value) {
        this.correoCliente = value;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String value) {
        this.telefonoCliente = value;
    }

    public String getDireccionCliente() {
        return direccionCliente;
    }

    public void setDireccionCliente(String value) {
        this.direccionCliente = value;
    }

    public String getRegionCliente() {
        return regionCliente;
    }

    public void setRegionCliente(String value) {
        this.regionCliente = value;
    }

    public String getComunaCliente() {
        return comunaCliente;
    }

    public void setComunaCliente(String value) {
        this.comunaCliente = value;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String value) {
        this.modalidad = value;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal value) {
        this.total = value;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String value) {
        this.estadoPago = value;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String value) {
        this.metodoPago = value;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime value) {
        this.fechaCreacion = value;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String value) {
        this.idUsuario = value;
    }
}
