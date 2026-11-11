package cl.nutrivida.usuarios.config;

import cl.nutrivida.usuarios.repository.SesionRepository;
import cl.nutrivida.usuarios.repository.UsuarioRepository;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.*;

@Component
public class SessionGuard extends OncePerRequestFilter {
    private final UsuarioRepository usuarioRepository;
    private final SesionRepository sesionRepository;

    public SessionGuard(UsuarioRepository usuarioRepository, SesionRepository sesionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.sesionRepository = sesionRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication()
                instanceof JwtAuthenticationToken auth) {
            var token = auth.getToken();
            var user = usuarioRepository.findById(token.getSubject());
            String sid = token.getClaimAsString("sid");
            var session =
                    sid == null
                            ? java.util.Optional.<cl.nutrivida.usuarios.model.SesionRefresh>empty()
                            : sesionRepository.findById(sid);
            boolean active =
                    user.isPresent()
                            && Boolean.TRUE.equals(user.get().getActivo())
                            && user.get().getRol().equals(token.getClaimAsString("rol"))
                            && session.isPresent()
                            && session.get().getUsuario().equals(token.getSubject())
                            && session.get()
                                    .getVenceEn()
                                    .isAfter(LocalDateTime.now(ZoneOffset.UTC));
            if (!active) {
                SecurityContextHolder.clearContext();
                res.sendError(401);
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
