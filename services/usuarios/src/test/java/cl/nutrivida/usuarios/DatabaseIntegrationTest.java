package cl.nutrivida.usuarios;

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
                .isEqualTo("nutrivida_usuarios");
    }

    @Test
    void todasLasTablasExisten() {
        for (String table : new String[] {"usuario"})
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Long.class))
                    .isNotNegative();
    }

    @Test
    void noPuedeLeerOtraBase() {
        assertThatThrownBy(() ->
                        jdbc.queryForObject(
                                "SELECT COUNT(*) FROM nutrivida_catalogo.categoria", Long.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
    }
}
