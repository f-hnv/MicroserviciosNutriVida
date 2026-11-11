package cl.nutrivida.ventas.repository;

import cl.nutrivida.ventas.model.DetallePedido;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, String> {
    java.util.List<DetallePedido> findByIdPedido(String idPedido);
}
