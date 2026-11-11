package cl.nutrivida.ventas.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class SessionGuard extends OncePerRequestFilter {
    private final RestClient users;

    public SessionGuard(@Value("${app.users-url}") String url) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));
        users = RestClient.builder().baseUrl(url).requestFactory(factory).build();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication()
                instanceof JwtAuthenticationToken auth) {
            try {
                users.get()
                        .uri("/api/v1/auth/me")
                        .headers(h -> h.setBearerAuth(auth.getToken().getTokenValue()))
                        .retrieve()
                        .toBodilessEntity();
            } catch (RestClientResponseException e) {
                SecurityContextHolder.clearContext();
                res.sendError(e.getStatusCode().value() >= 500 ? 503 : 401);
                return;
            } catch (RestClientException e) {
                res.sendError(503, "No se pudo validar la sesión");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
