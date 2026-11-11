package cl.nutrivida.usuarios;

import static org.assertj.core.api.Assertions.*;

import cl.nutrivida.usuarios.repository.UsuarioRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTest {
    @Autowired TestRestTemplate http;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired org.springframework.transaction.PlatformTransactionManager transactions;
    private final List<String> created = new ArrayList<>();
    private final String password = "ClaveDePrueba2026!";

    private Map<String, Object> registrar() {
        String suffix = UUID.randomUUID().toString();
        String run = String.format("8%07d-1", Math.floorMod(System.nanoTime(), 10_000_000));
        var data =
                Map.of(
                        "nombres",
                        "Paciente",
                        "apellidos",
                        "Prueba",
                        "correo",
                        "prueba." + suffix + "@gmail.com",
                        "password",
                        password,
                        "run",
                        run,
                        "telefono",
                        "912345678",
                        "direccion",
                        "Dirección de prueba",
                        "region",
                        "Valparaíso",
                        "comuna",
                        "Viña del Mar",
                        "rol",
                        "ADMIN");
        var result = http.postForEntity("/api/v1/auth/registro", data, Map.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        @SuppressWarnings("unchecked")
        Map<String, Object> user = result.getBody();
        created.add((String) user.get("id"));
        return user;
    }

    private ResponseEntity<Map> login(Map<String, Object> user) {
        return http.postForEntity(
                "/api/v1/auth/login",
                Map.of("correo", user.get("correo"), "password", password),
                Map.class);
    }

    @Test
    void registroPublicoNoPermiteElegirRolNiLeerUsuarios() {
        var user = registrar();
        assertThat(user.get("rol")).isEqualTo("paciente");
        assertThat(
                        usuarioRepository
                                .findById((String) user.get("id"))
                                .orElseThrow()
                                .getPasswordHash())
                .startsWith("$2")
                .isNotEqualTo(password);
        var token = (String) login(user).getBody().get("accessToken");
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        assertThat(
                        http.exchange(
                                        "/api/v1/usuarios",
                                        HttpMethod.GET,
                                        new HttpEntity<>(headers),
                                        String.class)
                                .getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void renuevaUnaSolaVezYCerrarSesionRevocaElAcceso() {
        var user = registrar();
        var initial = login(user);
        assertThat(initial.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = (String) initial.getBody().get("accessToken");
        String cookie = initial.getHeaders().getFirst(HttpHeaders.SET_COOKIE).split(";", 2)[0];
        assertThat(initial.getHeaders().getFirst(HttpHeaders.SET_COOKIE))
                .contains("HttpOnly")
                .contains("SameSite=Strict");
        var refreshHeaders = new HttpHeaders();
        refreshHeaders.set(HttpHeaders.COOKIE, cookie);
        refreshHeaders.set("X-NutriVida-Client", "web");
        var refreshed =
                http.exchange(
                        "/api/v1/auth/refresh",
                        HttpMethod.POST,
                        new HttpEntity<>(refreshHeaders),
                        Map.class);
        assertThat(refreshed.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(
                        http.exchange(
                                        "/api/v1/auth/refresh",
                                        HttpMethod.POST,
                                        new HttpEntity<>(refreshHeaders),
                                        String.class)
                                .getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        refreshHeaders.set(
                HttpHeaders.COOKIE,
                refreshed.getHeaders().getFirst(HttpHeaders.SET_COOKIE).split(";", 2)[0]);
        assertThat(
                        http.exchange(
                                        "/api/v1/auth/logout",
                                        HttpMethod.POST,
                                        new HttpEntity<>(refreshHeaders),
                                        String.class)
                                .getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        assertThat(
                        http.exchange(
                                        "/api/v1/auth/me",
                                        HttpMethod.GET,
                                        new HttpEntity<>(headers),
                                        String.class)
                                .getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void rechazaContrasenaIncorrecta() {
        var user = registrar();
        assertThat(
                        http.postForEntity(
                                        "/api/v1/auth/login",
                                        Map.of(
                                                "correo",
                                                user.get("correo"),
                                                "password",
                                                "incorrecta"),
                                        String.class)
                                .getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @AfterEach
    void limpiar() {
        new TransactionTemplate(transactions)
                .executeWithoutResult(status -> created.forEach(usuarioRepository::deleteById));
    }
}
