package cl.nutrivida.catalogo;

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
                .isEqualTo("nutrivida_catalogo");
    }

    @Test
    void todasLasTablasExisten() {
        for (String table : new String[] {"categoria", "servicio"})
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
    void servicioNoPuedeReferirCategoriaInexistente() {
        assertThatThrownBy(() ->
                        jdbc.update(
                                "INSERT INTO servicio"
                                    + " (id_servicio,titulo,descripcion,precio,descuento_porcentaje,modalidad,duracion_minutos,id_categoria)"
                                    + " VALUES (?, 'Prueba', 'Prueba', 25000, 0, 'ONLINE', 45, ?)",
                                java.util.UUID.randomUUID().toString(),
                                java.util.UUID.randomUUID().toString()))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void categoriaConServiciosNoSePuedeBorrar() {
        String categoria = java.util.UUID.randomUUID().toString();
        String servicio = java.util.UUID.randomUUID().toString();
        jdbc.update(
                "INSERT INTO categoria (id_categoria,nombre) VALUES (?,?)",
                categoria,
                "Prueba " + categoria);
        jdbc.update(
                "INSERT INTO servicio"
                    + " (id_servicio,titulo,descripcion,precio,descuento_porcentaje,modalidad,duracion_minutos,id_categoria)"
                    + " VALUES (?, 'Prueba', 'Prueba', 25000, 0, 'ONLINE', 45, ?)",
                servicio,
                categoria);
        assertThatThrownBy(
                        () -> jdbc.update("DELETE FROM categoria WHERE id_categoria=?", categoria))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }
}
