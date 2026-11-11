package cl.nutrivida.atencion.model;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_progreso")
public class RegistroProgreso {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_registro_progreso", nullable = false, columnDefinition = "char(36)")
    private String idRegistroProgreso;

    @Column(name = "fecha", nullable = false, columnDefinition = "date")
    private LocalDate fecha;

    @Column(name = "peso_kg", nullable = false, columnDefinition = "decimal(5,2)")
    private BigDecimal pesoKg;

    @Column(name = "fecha_creacion", nullable = false, columnDefinition = "datetime(6)")
    @CreationTimestamp(source = org.hibernate.annotations.SourceType.DB)
    private LocalDateTime fechaCreacion;

    @Column(name = "id_paciente", nullable = false, columnDefinition = "char(36)")
    private String idPaciente;

    public String getIdRegistroProgreso() {
        return idRegistroProgreso;
    }

    public void setIdRegistroProgreso(String value) {
        this.idRegistroProgreso = value;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate value) {
        this.fecha = value;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal value) {
        this.pesoKg = value;
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
}
