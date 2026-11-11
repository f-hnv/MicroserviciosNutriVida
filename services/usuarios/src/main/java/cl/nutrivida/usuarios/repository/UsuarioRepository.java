package cl.nutrivida.usuarios.repository;

import cl.nutrivida.usuarios.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    java.util.Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByRun(String run);

    long countByRolAndActivoTrue(String rol);

    @org.springframework.data.jpa.repository.Lock(
            jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from Usuario u where u.rol = 'ADMIN'")
    java.util.List<Usuario> bloquearAdministradores();
}
