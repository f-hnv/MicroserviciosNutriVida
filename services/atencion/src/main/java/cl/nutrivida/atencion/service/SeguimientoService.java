package cl.nutrivida.atencion.service;

import cl.nutrivida.atencion.client.DirectorioClient;
import cl.nutrivida.atencion.dto.PacienteResponseDTO;
import cl.nutrivida.atencion.dto.PlanRequestDTO;
import cl.nutrivida.atencion.dto.PlanResponseDTO;
import cl.nutrivida.atencion.dto.ProgresoRequestDTO;
import cl.nutrivida.atencion.dto.ProgresoResponseDTO;
import cl.nutrivida.atencion.model.Cita;
import cl.nutrivida.atencion.model.PlanAlimenticio;
import cl.nutrivida.atencion.model.RegistroProgreso;
import cl.nutrivida.atencion.repository.CitaRepository;
import cl.nutrivida.atencion.repository.PlanAlimenticioRepository;
import cl.nutrivida.atencion.repository.RegistroProgresoRepository;

import jakarta.validation.constraints.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Service
public class SeguimientoService {
    private final PlanAlimenticioRepository planRepository;
    private final RegistroProgresoRepository progresoRepository;
    private final CitaRepository citaRepository;
    private final DirectorioClient directorioClient;

    public SeguimientoService(
            PlanAlimenticioRepository planRepository,
            RegistroProgresoRepository progresoRepository,
            CitaRepository citaRepository,
            DirectorioClient directorioClient) {
        this.planRepository = planRepository;
        this.progresoRepository = progresoRepository;
        this.citaRepository = citaRepository;
        this.directorioClient = directorioClient;
    }

    private boolean admin(Jwt jwt) {
        return "ADMIN".equals(jwt.getClaimAsString("rol"));
    }

    private boolean medico(Jwt jwt) {
        return "MEDICO".equals(jwt.getClaimAsString("rol"));
    }

    public List<PacienteResponseDTO> pacientes(Jwt jwt) {
        if (!medico(jwt) && !admin(jwt)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return citaRepository.findAll().stream()
                .filter(c -> admin(jwt) || c.getIdProfesional().equals(jwt.getSubject()))
                .map(Cita::getIdPaciente)
                .distinct()
                .map(id ->
                        new PacienteResponseDTO(
                                id, directorioClient.persona(id, jwt.getTokenValue()).nombre()))
                .toList();
    }

    public List<PlanResponseDTO> planes(Jwt jwt) {
        return planRepository.findAll().stream()
                .filter(p ->
                        admin(jwt)
                                || p.getIdPaciente().equals(jwt.getSubject())
                                || p.getIdProfesional().equals(jwt.getSubject()))
                .sorted(Comparator.comparing(PlanAlimenticio::getFechaCreacion).reversed())
                .map(p -> vista(p, jwt))
                .toList();
    }

    @Transactional
    public PlanResponseDTO asignar(PlanRequestDTO data, Jwt jwt) {
        if (!medico(jwt) && !admin(jwt)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        var patient = directorioClient.persona(data.pacienteId(), jwt.getTokenValue());
        if (!patient.activo() || !"PACIENTE".equals(patient.rol()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paciente no disponible");
        if (!admin(jwt)
                && !citaRepository.existsByIdPacienteAndIdProfesional(
                        data.pacienteId(), jwt.getSubject()))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El paciente no pertenece a tu agenda");
        for (var previous : planRepository.findAll())
            if (previous.getIdPaciente().equals(data.pacienteId())
                    && Boolean.TRUE.equals(previous.getActivo())) {
                previous.setActivo(false);
                planRepository.save(previous);
            }
        String professional = admin(jwt) ? data.profesionalId() : jwt.getSubject();
        if (professional == null || professional.isBlank())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Selecciona el profesional autor del plan");
        var author = directorioClient.persona(professional, jwt.getTokenValue());
        if (!author.activo() || !"MEDICO".equals(author.rol()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profesional no válido");
        var p = new PlanAlimenticio();
        p.setIdPaciente(data.pacienteId());
        p.setIdProfesional(professional);
        p.setTitulo(data.titulo().strip());
        p.setContenido(data.contenido().strip());
        p.setActivo(true);
        return vista(planRepository.saveAndFlush(p), jwt);
    }

    @Transactional
    public void eliminarPlan(String id, Jwt jwt) {
        var p =
                planRepository
                        .findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!admin(jwt) && !(medico(jwt) && p.getIdProfesional().equals(jwt.getSubject())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        p.setActivo(false);
        planRepository.save(p);
    }

    public List<ProgresoResponseDTO> progreso(Jwt jwt) {
        return progresoRepository.findAll().stream()
                .filter(p -> p.getIdPaciente().equals(jwt.getSubject()))
                .sorted(Comparator.comparing(RegistroProgreso::getFecha))
                .map(p ->
                        new ProgresoResponseDTO(
                                p.getIdRegistroProgreso(), p.getFecha(), p.getPesoKg()))
                .toList();
    }

    @Transactional
    public ProgresoResponseDTO guardarProgreso(ProgresoRequestDTO data, Jwt jwt) {
        if (data.fecha().isAfter(LocalDate.now(CitaService.CHILE)))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La fecha no puede ser futura");
        var p = new RegistroProgreso();
        p.setIdPaciente(jwt.getSubject());
        p.setFecha(data.fecha());
        p.setPesoKg(data.peso());
        progresoRepository.saveAndFlush(p);
        return new ProgresoResponseDTO(p.getIdRegistroProgreso(), p.getFecha(), p.getPesoKg());
    }

    @Transactional
    public void eliminarProgreso(String id, Jwt jwt) {
        var p =
                progresoRepository
                        .findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!admin(jwt) && !p.getIdPaciente().equals(jwt.getSubject()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        progresoRepository.delete(p);
    }

    private PlanResponseDTO vista(PlanAlimenticio p, Jwt jwt) {
        return new PlanResponseDTO(
                p.getIdPlanAlimenticio(),
                p.getIdPaciente(),
                p.getTitulo(),
                p.getContenido(),
                directorioClient.persona(p.getIdProfesional(), jwt.getTokenValue()).nombre(),
                p.getFechaCreacion().toLocalDate(),
                Boolean.TRUE.equals(p.getActivo()));
    }
}
