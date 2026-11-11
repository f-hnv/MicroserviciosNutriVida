package cl.nutrivida.usuarios.repository;

import cl.nutrivida.usuarios.model.SesionRefresh;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface SesionRepository extends JpaRepository<SesionRefresh, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SesionRefresh> findByTokenHash(String tokenHash);

    void deleteByUsuario(String usuario);
}
