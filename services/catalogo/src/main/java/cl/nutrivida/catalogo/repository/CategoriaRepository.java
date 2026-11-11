package cl.nutrivida.catalogo.repository;

import cl.nutrivida.catalogo.model.Categoria;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, String> {}
