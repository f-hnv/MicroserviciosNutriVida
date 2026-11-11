package cl.nutrivida.ventas.repository;

import cl.nutrivida.ventas.model.Pedido;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, String> {
    java.util.Optional<Pedido> findByClaveIdempotencia(String clave);
}
