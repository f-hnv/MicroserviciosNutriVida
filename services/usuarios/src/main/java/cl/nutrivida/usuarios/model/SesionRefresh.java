package cl.nutrivida.usuarios.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sesion_refresh")
public class SesionRefresh {
    @Id
    @Column(name = "id_sesion", columnDefinition = "char(36)")
    private String id;

    @Column(name = "id_usuario", nullable = false, columnDefinition = "char(36)")
    private String usuario;

    @Column(name = "token_hash", nullable = false, columnDefinition = "char(64)")
    private String tokenHash;

    @Column(name = "vence_en", nullable = false)
    private LocalDateTime venceEn;

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String value) {
        usuario = value;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String value) {
        tokenHash = value;
    }

    public LocalDateTime getVenceEn() {
        return venceEn;
    }

    public void setVenceEn(LocalDateTime value) {
        venceEn = value;
    }
}
