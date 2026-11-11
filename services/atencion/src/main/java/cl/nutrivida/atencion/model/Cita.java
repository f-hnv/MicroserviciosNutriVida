package cl.nutrivida.atencion.model;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cita")
public class Cita {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_cita", nullable = false, columnDefinition = "char(36)")
    private String idCita;

    @Column(name = "servicio_nombre", nullable = false, columnDefinition = "varchar(150)")
    private String servicioNombre;

    @Column(name = "inicio_utc", nullable = false, columnDefinition = "datetime(6)")
    private LocalDateTime inicioUtc;

    @Column(name = "duracion_minutos", nullable = false, columnDefinition = "smallint")
    private Short duracionMinutos;

    @Column(name = "precio_acordado", nullable = false, columnDefinition = "decimal(12,2)")
    private BigDecimal precioAcordado;

    @Column(name = "modalidad", nullable = false, columnDefinition = "varchar(16)")
    private String modalidad;

    @Column(name = "motivo", nullable = true, columnDefinition = "varchar(500)")
    private String motivo;

    @Column(name = "estado", nullable = false, columnDefinition = "varchar(16)")
    private String estado;

    @Column(name = "fecha_creacion", nullable = false, columnDefinition = "datetime(6)")
    @CreationTimestamp(source = org.hibernate.annotations.SourceType.DB)
    private LocalDateTime fechaCreacion;

    @Column(name = "id_paciente", nullable = false, columnDefinition = "char(36)")
    private String idPaciente;

    @Column(name = "id_profesional", nullable = false, columnDefinition = "char(36)")
    private String idProfesional;

    @Column(name = "id_servicio", nullable = false, columnDefinition = "char(36)")
    private String idServicio;

    public String getIdCita() {
        return idCita;
    }

    public void setIdCita(String value) {
        this.idCita = value;
    }

    public String getServicioNombre() {
        return servicioNombre;
    }

    public void setServicioNombre(String value) {
        this.servicioNombre = value;
    }

    public LocalDateTime getInicioUtc() {
        return inicioUtc;
    }

    public void setInicioUtc(LocalDateTime value) {
        this.inicioUtc = value;
    }

    public Short getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Short value) {
        this.duracionMinutos = value;
    }

    public BigDecimal getPrecioAcordado() {
        return precioAcordado;
    }

    public void setPrecioAcordado(BigDecimal value) {
        this.precioAcordado = value;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String value) {
        this.modalidad = value;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String value) {
        this.motivo = value;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String value) {
        this.estado = value;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime value) {
        this.fechaCreacion = value;
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(String value) {
        this.idPaciente = value;
    }

    public String getIdProfesional() {
        return idProfesional;
    }

    public void setIdProfesional(String value) {
        this.idProfesional = value;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String value) {
        this.idServicio = value;
    }
}
