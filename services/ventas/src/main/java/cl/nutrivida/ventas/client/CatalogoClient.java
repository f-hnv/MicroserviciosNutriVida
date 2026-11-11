package cl.nutrivida.ventas.client;

import cl.nutrivida.ventas.dto.ServicioCatalogoResponseDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Component
public class CatalogoClient {
    private final RestClient catalogoClient;

    public CatalogoClient(@Value("${app.catalog-url:http://127.0.0.1:8082}") String url) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));
        catalogoClient = RestClient.builder().baseUrl(url).requestFactory(factory).build();
    }

    public ServicioCatalogoResponseDTO obtenerServicio(String id) {
        try {
            return catalogoClient
                    .get()
                    .uri("/api/v1/servicios/{id}", id)
                    .retrieve()
                    .body(ServicioCatalogoResponseDTO.class);
        } catch (RestClientResponseException exception) {
            throw new ResponseStatusException(
                    exception.getStatusCode(), "Hay un servicio no disponible");
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Catálogo no disponible");
        }
    }
}
