package cl.nutrivida.atencion.service;

import cl.nutrivida.atencion.client.DirectorioClient;
import cl.nutrivida.atencion.dto.CitaRequestDTO;
import cl.nutrivida.atencion.dto.CitaResponseDTO;
import cl.nutrivida.atencion.dto.EstadoCitaRequestDTO;
import cl.nutrivida.atencion.dto.ReprogramarCitaRequestDTO;
import cl.nutrivida.atencion.model.Cita;
import cl.nutrivida.atencion.repository.CitaRepository;

import jakarta.validation.constraints.*;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.RoundingMode;
import java.sql.*;
import java.time.*;
import java.util.*;

import javax.sql.DataSource;

@Service
public class CitaService {
    public static final ZoneId CHILE = ZoneId.of("America/Santiago");
    private final CitaRepository citas;
    private final DirectorioClient directorioClient;
    private final DataSource dataSource;

    public CitaService(
            CitaRepository citas, DirectorioClient directorioClient, DataSource dataSource) {
        this.citas = citas;
        this.directorioClient = directorioClient;
        this.dataSource = dataSource;
    }

    private boolean admin(Jwt jwt) {
        return "ADMIN".equals(jwt.getClaimAsString("rol"));
    }

    private LocalDateTime utc(LocalDate date, LocalTime time) {
        return LocalDateTime.ofInstant(
                ZonedDateTime.of(date, time, CHILE).toInstant(), ZoneOffset.UTC);
    }

    private LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    private void fechaValida(LocalDate date) {
        if (date.isBefore(LocalDate.now(CHILE))
                || date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Selecciona un día hábil que no sea anterior a hoy");
    }

    public List<String> horarios(String servicioId, LocalDate date, Jwt jwt) {
        var service = directorioClient.servicio(servicioId, jwt.getTokenValue());
        if (!service.activo() || service.profesionalId() == null) return List.of();
        return horarios(service.profesionalId(), service.duracion(), date, null);
    }

    private List<String> horarios(
            String professional, int duration, LocalDate date, String exclude) {
        fechaValida(date);
        var start = utc(date, LocalTime.MIN);
        var end = utc(date.plusDays(1), LocalTime.MIN);
        var occupied =
                citas.findByIdProfesionalAndInicioUtcBetween(professional, start, end).stream()
                        .filter(c ->
                                !"CANCELADA".equals(c.getEstado())
                                        && !Objects.equals(exclude, c.getIdCita()))
                        .toList();
        List<String> result = new ArrayList<>();
        for (int minute = 9 * 60; minute + duration <= 19 * 60; minute += 30) {
            var time = LocalTime.of(minute / 60, minute % 60);
            var candidate = utc(date, time);
            var candidateEnd = candidate.plusMinutes(duration);
            if (candidate.isAfter(now())
                    && occupied.stream()
                            .noneMatch(c ->
                                    candidate.isBefore(
                                                    c.getInicioUtc()
                                                            .plusMinutes(c.getDuracionMinutos()))
                                            && candidateEnd.isAfter(c.getInicioUtc())))
                result.add(time.toString());
        }
        return result;
    }

    @Transactional
    public CitaResponseDTO reservar(CitaRequestDTO data, Jwt jwt) {
        var patient = directorioClient.persona(jwt.getSubject(), jwt.getTokenValue());
        if (!patient.activo() || !"PACIENTE".equals(patient.rol()))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "La reserva requiere una cuenta de paciente");
        var service = directorioClient.servicio(data.servicioId(), jwt.getTokenValue());
        if (!service.activo() || service.profesionalId() == null)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El servicio no tiene un profesional disponible");
        var professional = directorioClient.persona(service.profesionalId(), jwt.getTokenValue());
        if (!professional.activo() || !"MEDICO".equals(professional.rol()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profesional no disponible");
        bloquear(service.profesionalId());
        if (!horarios(service.profesionalId(), service.duracion(), data.fecha(), null)
                .contains(data.hora()))
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El horario ya no está disponible");
        Cita c = new Cita();
        c.setIdPaciente(jwt.getSubject());
        c.setIdProfesional(service.profesionalId());
        c.setIdServicio(service.id());
        c.setServicioNombre(service.titulo());
        c.setInicioUtc(utc(data.fecha(), LocalTime.parse(data.hora())));
        c.setDuracionMinutos((short) service.duracion());
        c.setPrecioAcordado(
                service.precio()
                        .multiply(
                                java.math.BigDecimal.ONE.subtract(
                                        service.descuento().movePointLeft(2)))
                        .setScale(0, RoundingMode.HALF_UP));
        c.setModalidad(service.modalidad().toUpperCase(Locale.ROOT));
        c.setMotivo(data.motivo() == null ? "" : data.motivo().strip());
        c.setEstado("PENDIENTE");
        return vista(citas.saveAndFlush(c), jwt);
    }

    public List<CitaResponseDTO> listar(Jwt jwt) {
        return citas.findAll().stream()
                .filter(c ->
                        admin(jwt)
                                || c.getIdPaciente().equals(jwt.getSubject())
                                || c.getIdProfesional().equals(jwt.getSubject()))
                .sorted(Comparator.comparing(Cita::getInicioUtc).reversed())
                .map(c -> vista(c, jwt))
                .toList();
    }

    public CitaResponseDTO ver(String id, Jwt jwt) {
        Cita c = obtener(id);
        permiso(c, jwt, true);
        return vista(c, jwt);
    }

    @Transactional
    public CitaResponseDTO reprogramar(String id, ReprogramarCitaRequestDTO data, Jwt jwt) {
        Cita c = obtenerEdicion(id);
        permiso(c, jwt, false);
        terminal(c);
        bloquear(c.getIdProfesional());
        if (!horarios(c.getIdProfesional(), c.getDuracionMinutos(), data.fecha(), id)
                .contains(data.hora()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Horario no disponible");
        c.setInicioUtc(utc(data.fecha(), LocalTime.parse(data.hora())));
        return vista(citas.saveAndFlush(c), jwt);
    }

    @Transactional
    public CitaResponseDTO estado(String id, EstadoCitaRequestDTO data, Jwt jwt) {
        Cita c = obtenerEdicion(id);
        if (!admin(jwt)
                && !("MEDICO".equals(jwt.getClaimAsString("rol"))
                        && c.getIdProfesional().equals(jwt.getSubject())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        terminal(c);
        if ("PENDIENTE".equals(data.estado()))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No se puede regresar una cita a pendiente");
        if ("COMPLETADA".equals(data.estado()) && c.getInicioUtc().isAfter(now()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La cita aún no ha ocurrido");
        c.setEstado(data.estado());
        return vista(citas.saveAndFlush(c), jwt);
    }

    @Transactional
    public void cancelar(String id, Jwt jwt) {
        Cita c = obtenerEdicion(id);
        permiso(c, jwt, false);
        terminal(c);
        c.setEstado("CANCELADA");
        citas.save(c);
    }

    private void terminal(Cita c) {
        if (List.of("COMPLETADA", "CANCELADA").contains(c.getEstado()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La cita ya está finalizada");
    }

    private void permiso(Cita c, Jwt jwt, boolean read) {
        if (!admin(jwt)
                && !c.getIdPaciente().equals(jwt.getSubject())
                && !(read && c.getIdProfesional().equals(jwt.getSubject())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    private Cita obtenerEdicion(String id) {
        return citas.bloquearPorId(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    private Cita obtener(String id) {
        return citas.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    private CitaResponseDTO vista(Cita c, Jwt jwt) {
        var local = c.getInicioUtc().atOffset(ZoneOffset.UTC).atZoneSameInstant(CHILE);
        return new CitaResponseDTO(
                c.getIdCita(),
                c.getIdPaciente(),
                c.getIdProfesional(),
                directorioClient.persona(c.getIdPaciente(), jwt.getTokenValue()).nombre(),
                directorioClient.persona(c.getIdProfesional(), jwt.getTokenValue()).nombre(),
                c.getIdServicio(),
                c.getServicioNombre(),
                local.toLocalDate(),
                local.toLocalTime().toString(),
                c.getDuracionMinutos(),
                c.getPrecioAcordado(),
                c.getModalidad(),
                c.getEstado(),
                c.getMotivo());
    }

    private void bloquear(String professional) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String name = "agenda-" + professional;
        try (var stmt = connection.prepareStatement("SELECT GET_LOCK(?, 3)")) {
            stmt.setString(1, name);
            try (var rs = stmt.executeQuery()) {
                if (!rs.next() || rs.getInt(1) != 1)
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "La agenda está siendo actualizada; intenta nuevamente");
            }
        } catch (SQLException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "No se pudo bloquear la agenda");
        }
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        try (var stmt = connection.prepareStatement("SELECT RELEASE_LOCK(?)")) {
                            stmt.setString(1, name);
                            stmt.execute();
                        } catch (SQLException e) {
                            throw new IllegalStateException(
                                    "No se pudo liberar el bloqueo de agenda", e);
                        }
                    }
                });
    }
}
