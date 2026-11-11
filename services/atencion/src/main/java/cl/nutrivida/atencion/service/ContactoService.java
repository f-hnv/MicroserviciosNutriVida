package cl.nutrivida.atencion.service;

import cl.nutrivida.atencion.dto.ContactoRequestDTO;
import cl.nutrivida.atencion.dto.ContactoResponseDTO;
import cl.nutrivida.atencion.model.MensajeContacto;
import cl.nutrivida.atencion.repository.MensajeContactoRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ContactoService {
    private final MensajeContactoRepository mensajeRepository;

    public ContactoService(MensajeContactoRepository mensajeRepository) {
        this.mensajeRepository = mensajeRepository;
    }

    public ContactoResponseDTO crear(ContactoRequestDTO request) {
        MensajeContacto mensaje = new MensajeContacto();
        mensaje.setNombre(request.nombre().strip());
        mensaje.setCorreo(request.correo().strip().toLowerCase(Locale.ROOT));
        mensaje.setComentario(request.comentario().strip());
        MensajeContacto guardado = mensajeRepository.saveAndFlush(mensaje);
        return new ContactoResponseDTO(
                guardado.getId(), "Tu consulta quedó guardada en la clínica");
    }

    public List<MensajeContacto> listar() {
        return mensajeRepository.findAll();
    }
}
