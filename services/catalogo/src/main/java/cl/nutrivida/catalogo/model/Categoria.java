package cl.nutrivida.catalogo.model;

import jakarta.persistence.*;

import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_categoria", nullable = false, columnDefinition = "char(36)")
    private String idCategoria;

    @Column(name = "nombre", nullable = false, columnDefinition = "varchar(100)")
    private String nombre;

    @Column(name = "activo", nullable = false, columnDefinition = "bit(1)")
    private Boolean activo;

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String value) {
        this.idCategoria = value;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String value) {
        this.nombre = value;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean value) {
        this.activo = value;
    }
}
