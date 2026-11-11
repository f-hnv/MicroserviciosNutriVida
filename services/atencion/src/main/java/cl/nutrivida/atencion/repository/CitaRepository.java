package cl.nutrivida.atencion.repository;

import cl.nutrivida.atencion.model.Cita;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CitaRepository extends JpaRepository<Cita, String> {
    java.util.List<Cita> findByIdProfesionalAndInicioUtcBetween(
            String profesional, java.time.LocalDateTime desde, java.time.LocalDateTime hasta);

    boolean existsByIdPacienteAndIdProfesional(String paciente, String profesional);

    @org.springframework.data.jpa.repository.Lock(
            jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select c from Cita c where c.idCita = :id")
    java.util.Optional<Cita> bloquearPorId(
            @org.springframework.data.repository.query.Param("id") String id);
}
