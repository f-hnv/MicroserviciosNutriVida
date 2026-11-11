package cl.nutrivida.ventas;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DatabaseIntegrationTest {
    @Autowired JdbcTemplate jdbc;

    @Test
    void conectaAlSchemaDelServicio() {
        assertThat(jdbc.queryForObject("SELECT DATABASE()", String.class))
                .isEqualTo("nutrivida_ventas");
    }

    @Test
    void todasLasTablasExisten() {
        for (String table : new String[] {"pedido", "detalle_pedido"})
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Long.class))
                    .isNotNegative();
    }

    @Test
    void noPuedeLeerOtraBase() {
        assertThatThrownBy(() ->
                        jdbc.queryForObject(
                                "SELECT COUNT(*) FROM nutrivida_usuarios.usuario", Long.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
    }

    @Test
    void detalleNoPuedeReferirPedidoInexistente() {
        assertThatThrownBy(() ->
                        jdbc.update(
                                "INSERT INTO detalle_pedido"
                                    + " (id_detalle_pedido,titulo_servicio,cantidad,precio_base,descuento_porcentaje,precio_unitario,id_pedido,id_servicio)"
                                    + " VALUES (?, 'Prueba', 1, 25000, 0, 25000, ?, ?)",
                                java.util.UUID.randomUUID().toString(),
                                java.util.UUID.randomUUID().toString(),
                                java.util.UUID.randomUUID().toString()))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }
}
