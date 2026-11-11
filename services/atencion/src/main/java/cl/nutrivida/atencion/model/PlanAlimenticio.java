package cl.nutrivida.atencion.model;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "plan_alimenticio")
public class PlanAlimenticio {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_plan_alimenticio", nullable = false, columnDefinition = "char(36)")
    private String idPlanAlimenticio;

    @Column(name = "titulo", nullable = false, columnDefinition = "varchar(150)")
    private String titulo;

    @Column(name = "contenido", nullable = false, columnDefinition = "text")
    private String contenido;

    @Column(name = "activo", nullable = false, columnDefinition = "bit(1)")
    private Boolean activo;

    @Column(name = "fecha_creacion", nullable = false, columnDefinition = "datetime(6)")
    @CreationTimestamp(source = org.hibernate.annotations.SourceType.DB)
    private LocalDateTime fechaCreacion;

    @Column(name = "id_paciente", nullable = false, columnDefinition = "char(36)")
    private String idPaciente;

    @Column(name = "id_profesional", nullable = false, columnDefinition = "char(36)")
    private String idProfesional;

    public String getIdPlanAlimenticio() {
        return idPlanAlimenticio;
    }

    public void setIdPlanAlimenticio(String value) {
        this.idPlanAlimenticio = value;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String value) {
        this.titulo = value;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String value) {
        this.contenido = value;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean value) {
        this.activo = value;
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
}
