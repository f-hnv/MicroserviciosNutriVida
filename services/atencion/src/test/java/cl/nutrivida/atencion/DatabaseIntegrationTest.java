package cl.nutrivida.atencion;

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
                .isEqualTo("nutrivida_atencion");
    }

    @Test
    void todasLasTablasExisten() {
        for (String table : new String[] {"cita", "plan_alimenticio", "registro_progreso"})
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
}
