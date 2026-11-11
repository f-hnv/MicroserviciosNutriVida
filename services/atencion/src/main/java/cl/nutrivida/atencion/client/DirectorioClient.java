package cl.nutrivida.atencion.client;

import cl.nutrivida.atencion.dto.ServicioCatalogoResponseDTO;
import cl.nutrivida.atencion.dto.UsuarioResumenResponseDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Component
public class DirectorioClient {
    private final RestClient users, catalog;

    public DirectorioClient(
            @Value("${app.users-url}") String userUrl,
            @Value("${app.catalog-url:http://127.0.0.1:8082}") String catalogUrl) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));
        users = RestClient.builder().baseUrl(userUrl).requestFactory(factory).build();
        catalog = RestClient.builder().baseUrl(catalogUrl).requestFactory(factory).build();
    }

    public UsuarioResumenResponseDTO persona(String id, String token) {
        try {
            return users.get()
                    .uri("/api/v1/usuarios/{id}/resumen", id)
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .body(UsuarioResumenResponseDTO.class);
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(e.getStatusCode(), "No se pudo validar el usuario");
        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Usuarios no disponible");
        }
    }

    public ServicioCatalogoResponseDTO servicio(String id, String token) {
        try {
            return catalog.get()
                    .uri("/api/v1/servicios/{id}", id)
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .body(ServicioCatalogoResponseDTO.class);
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(e.getStatusCode(), "Servicio no disponible");
        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Catálogo no disponible");
        }
    }
}
