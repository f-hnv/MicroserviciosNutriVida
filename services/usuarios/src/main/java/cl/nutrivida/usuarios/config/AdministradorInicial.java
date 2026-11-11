package cl.nutrivida.usuarios.config;

import cl.nutrivida.usuarios.model.Usuario;
import cl.nutrivida.usuarios.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdministradorInicial implements ApplicationRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String correo, password;

    public AdministradorInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin-email:}") String correo,
            @Value("${app.admin-password:}") String password) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.correo = correo;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (correo.isBlank() || password.isBlank() || usuarioRepository.count() > 0) return;
        if (password.length() < 12)
            throw new IllegalArgumentException("La clave inicial requiere al menos 12 caracteres");
        Usuario u = new Usuario();
        u.setNombres("Felipe");
        u.setApellidos("Araya");
        u.setCorreo(correo.toLowerCase());
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setRol("ADMIN");
        u.setActivo(true);
        u.setRun("00000000-0");
        u.setTelefono("000000000");
        u.setDireccion("Completar perfil");
        u.setRegion("Valparaíso");
        u.setComuna("Viña del Mar");
        usuarioRepository.save(u);
    }
}
