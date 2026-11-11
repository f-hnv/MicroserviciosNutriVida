package cl.nutrivida.atencion.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class EstadoService {
    private final JdbcTemplate jdbc;

    public EstadoService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean conectado() {
        return Integer.valueOf(1).equals(jdbc.queryForObject("SELECT 1", Integer.class));
    }
}
