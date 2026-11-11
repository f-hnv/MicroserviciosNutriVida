package cl.nutrivida.usuarios.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_usuario", nullable = false, columnDefinition = "char(36)")
    private String idUsuario;

    @Column(name = "nombres", nullable = false, columnDefinition = "varchar(100)")
    private String nombres;

    @Column(name = "apellidos", nullable = false, columnDefinition = "varchar(100)")
    private String apellidos;

    @Column(name = "correo", nullable = false, columnDefinition = "varchar(254)")
    private String correo;

    @Column(name = "password_hash", nullable = false, columnDefinition = "varchar(255)")
    @JsonIgnore
    private String passwordHash;

    @Column(name = "rol", nullable = false, columnDefinition = "varchar(24)")
    private String rol;

    @Column(name = "run", nullable = false, columnDefinition = "varchar(12)")
    private String run;

    @Column(name = "telefono", nullable = false, columnDefinition = "varchar(20)")
    private String telefono;

    @Column(name = "direccion", nullable = false, columnDefinition = "varchar(250)")
    private String direccion;

    @Column(name = "region", nullable = false, columnDefinition = "varchar(80)")
    private String region;

    @Column(name = "comuna", nullable = false, columnDefinition = "varchar(80)")
    private String comuna;

    @Column(name = "activo", nullable = false, columnDefinition = "bit(1)")
    private Boolean activo;

    @Column(name = "fecha_creacion", nullable = false, columnDefinition = "datetime(6)")
    @CreationTimestamp(source = org.hibernate.annotations.SourceType.DB)
    private LocalDateTime fechaCreacion;

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String value) {
        this.idUsuario = value;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String value) {
        this.nombres = value;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String value) {
        this.apellidos = value;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String value) {
        this.correo = value;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String value) {
        this.passwordHash = value;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String value) {
        this.rol = value;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String value) {
        this.run = value;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String value) {
        this.telefono = value;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String value) {
        this.direccion = value;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String value) {
        this.region = value;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String value) {
        this.comuna = value;
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
}
