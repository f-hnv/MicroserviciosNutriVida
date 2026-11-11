package cl.nutrivida.atencion.controller;

import cl.nutrivida.atencion.dto.ContactoRequestDTO;
import cl.nutrivida.atencion.dto.ContactoResponseDTO;
import cl.nutrivida.atencion.service.ContactoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contacto")
@Tag(name = "Contacto")
public class ContactoController {
    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @PostMapping
    @Operation(summary = "Guardar una consulta de contacto")
    public ResponseEntity<ContactoResponseDTO> crear(
            @Valid @RequestBody ContactoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactoService.crear(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consultar los mensajes recibidos")
    public ResponseEntity<List<cl.nutrivida.atencion.model.MensajeContacto>> listar() {
        return ResponseEntity.ok(contactoService.listar());
    }
}
