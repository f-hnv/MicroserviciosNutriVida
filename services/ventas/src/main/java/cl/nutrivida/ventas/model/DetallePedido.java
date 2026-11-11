package cl.nutrivida.ventas.model;

import jakarta.persistence.*;

import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_detalle_pedido", nullable = false, columnDefinition = "char(36)")
    private String idDetallePedido;

    @Column(name = "titulo_servicio", nullable = false, columnDefinition = "varchar(150)")
    private String tituloServicio;

    @Column(name = "cantidad", nullable = false, columnDefinition = "smallint")
    private Short cantidad;

    @Column(name = "precio_base", nullable = false, columnDefinition = "decimal(12,2)")
    private BigDecimal precioBase;

    @Column(name = "descuento_porcentaje", nullable = false, columnDefinition = "decimal(5,2)")
    private BigDecimal descuentoPorcentaje;

    @Column(name = "precio_unitario", nullable = false, columnDefinition = "decimal(12,2)")
    private BigDecimal precioUnitario;

    @Column(name = "id_pedido", nullable = false, columnDefinition = "char(36)")
    private String idPedido;

    @Column(name = "id_servicio", nullable = false, columnDefinition = "char(36)")
    private String idServicio;

    public String getIdDetallePedido() {
        return idDetallePedido;
    }

    public void setIdDetallePedido(String value) {
        this.idDetallePedido = value;
    }

    public String getTituloServicio() {
        return tituloServicio;
    }

    public void setTituloServicio(String value) {
        this.tituloServicio = value;
    }

    public Short getCantidad() {
        return cantidad;
    }

    public void setCantidad(Short value) {
        this.cantidad = value;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal value) {
        this.precioBase = value;
    }

    public BigDecimal getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(BigDecimal value) {
        this.descuentoPorcentaje = value;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal value) {
        this.precioUnitario = value;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String value) {
        this.idPedido = value;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String value) {
        this.idServicio = value;
    }
}
