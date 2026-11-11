package cl.nutrivida.catalogo.repository;

import cl.nutrivida.catalogo.model.Servicio;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, String> {
    boolean existsByIdCategoria(String idCategoria);
}
