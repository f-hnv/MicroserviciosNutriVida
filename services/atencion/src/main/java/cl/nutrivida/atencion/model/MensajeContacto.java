package cl.nutrivida.atencion.model;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje_contacto")
public class MensajeContacto {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_mensaje", columnDefinition = "char(36)")
    private String id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 254)
    private String correo;

    @Column(nullable = false, length = 500)
    private String comentario;

    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fecha;

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String value) {
        nombre = value;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String value) {
        correo = value;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String value) {
        comentario = value;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
