package cl.nutrivida.catalogo.client;

import cl.nutrivida.catalogo.dto.ProfesionalResponseDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProfesionalClient {
    private final RestClient usuariosClient;

    public ProfesionalClient(@Value("${app.users-url}") String usuariosUrl) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));
        usuariosClient = RestClient.builder().baseUrl(usuariosUrl).requestFactory(factory).build();
    }

    public Map<String, String> obtenerProfesionales() {
        try {
            ProfesionalResponseDTO[] profesionales =
                    usuariosClient
                            .get()
                            .uri("/api/v1/profesionales")
                            .retrieve()
                            .body(ProfesionalResponseDTO[].class);
            Map<String, String> resultado = new HashMap<>();
            if (profesionales != null) {
                for (ProfesionalResponseDTO profesional : profesionales) {
                    resultado.put(profesional.id(), profesional.nombre());
                }
            }
            return resultado;
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "No se pudo consultar a los profesionales");
        }
    }
}
