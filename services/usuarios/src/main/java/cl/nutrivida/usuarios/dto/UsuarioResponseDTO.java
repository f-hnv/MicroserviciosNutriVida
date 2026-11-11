package cl.nutrivida.usuarios.dto;

import cl.nutrivida.usuarios.model.Usuario;

public record UsuarioResponseDTO(
        String id,
        String nombre,
        String nombres,
        String apellidos,
        String correo,
        String rol,
        boolean esAdmin,
        String run,
        String telefono,
        String direccion,
        String region,
        String comuna,
        boolean activo) {
    public static UsuarioResponseDTO de(Usuario u) {
        return new UsuarioResponseDTO(
                u.getIdUsuario(),
                u.getNombres() + " " + u.getApellidos(),
                u.getNombres(),
                u.getApellidos(),
                u.getCorreo(),
                u.getRol().toLowerCase(),
                "ADMIN".equals(u.getRol()),
                u.getRun(),
                u.getTelefono(),
                u.getDireccion(),
                u.getRegion(),
                u.getComuna(),
                Boolean.TRUE.equals(u.getActivo()));
    }
}
