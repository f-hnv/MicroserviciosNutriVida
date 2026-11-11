package cl.nutrivida.catalogo.model;

import jakarta.persistence.*;

import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Entity
@Table(name = "servicio")
public class Servicio {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_servicio", nullable = false, columnDefinition = "char(36)")
    private String idServicio;

    @Column(name = "titulo", nullable = false, columnDefinition = "varchar(150)")
    private String titulo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "varchar(2000)")
    private String descripcion;

    @Column(name = "precio", nullable = false, columnDefinition = "decimal(12,2)")
    private BigDecimal precio;

    @Column(name = "descuento_porcentaje", nullable = false, columnDefinition = "decimal(5,2)")
    private BigDecimal descuentoPorcentaje;

    @Column(name = "modalidad", nullable = false, columnDefinition = "varchar(16)")
    private String modalidad;

    @Column(name = "duracion_minutos", nullable = false, columnDefinition = "smallint")
    private Short duracionMinutos;

    @Column(name = "imagen_url", nullable = true, columnDefinition = "varchar(1000)")
    private String imagenUrl;

    @Column(name = "activo", nullable = false, columnDefinition = "bit(1)")
    private Boolean activo;

    @Column(name = "id_categoria", nullable = false, columnDefinition = "char(36)")
    private String idCategoria;

    @Column(name = "id_profesional", nullable = true, columnDefinition = "char(36)")
    private String idProfesional;

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String value) {
        this.idServicio = value;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String value) {
        this.titulo = value;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String value) {
        this.descripcion = value;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal value) {
        this.precio = value;
    }

    public BigDecimal getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(BigDecimal value) {
        this.descuentoPorcentaje = value;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String value) {
        this.modalidad = value;
    }

    public Short getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Short value) {
        this.duracionMinutos = value;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String value) {
        this.imagenUrl = value;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean value) {
        this.activo = value;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String value) {
        this.idCategoria = value;
    }

    public String getIdProfesional() {
        return idProfesional;
    }

    public void setIdProfesional(String value) {
        this.idProfesional = value;
    }
}
