CREATE TABLE cita (
  id_cita CHAR(36) NOT NULL,
  servicio_nombre VARCHAR(150) NOT NULL,
  inicio_utc DATETIME(6) NOT NULL,
  duracion_minutos SMALLINT NOT NULL,
  precio_acordado DECIMAL(12,2) NOT NULL,
  modalidad VARCHAR(16) NOT NULL,
  motivo VARCHAR(500) NULL,
  estado VARCHAR(16) NOT NULL,
  fecha_creacion DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  id_paciente CHAR(36) NOT NULL,
  id_profesional CHAR(36) NOT NULL,
  id_servicio CHAR(36) NOT NULL,
  CONSTRAINT pk_cita PRIMARY KEY (id_cita),
  CHECK (precio_acordado >= 0),
  CHECK (duracion_minutos BETWEEN 1 AND 600),
  CHECK (modalidad IN ('PRESENCIAL','ONLINE')),
  CHECK (estado IN ('PENDIENTE','CONFIRMADA','COMPLETADA','CANCELADA'))
) ENGINE=InnoDB;
CREATE INDEX ix_cita_1 ON cita (id_profesional, inicio_utc, estado);
CREATE INDEX ix_cita_2 ON cita (id_paciente, inicio_utc);

CREATE TABLE plan_alimenticio (
  id_plan_alimenticio CHAR(36) NOT NULL,
  titulo VARCHAR(150) NOT NULL,
  contenido TEXT NOT NULL,
  activo BIT(1) NOT NULL DEFAULT b'1',
  fecha_creacion DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  id_paciente CHAR(36) NOT NULL,
  id_profesional CHAR(36) NOT NULL,
  CONSTRAINT pk_plan_alimenticio PRIMARY KEY (id_plan_alimenticio)
) ENGINE=InnoDB;
CREATE INDEX ix_plan_alimenticio_1 ON plan_alimenticio (id_paciente, activo);

CREATE TABLE registro_progreso (
  id_registro_progreso CHAR(36) NOT NULL,
  fecha DATE NOT NULL,
  peso_kg DECIMAL(5,2) NOT NULL,
  fecha_creacion DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  id_paciente CHAR(36) NOT NULL,
  CONSTRAINT pk_registro_progreso PRIMARY KEY (id_registro_progreso),
  CONSTRAINT uq_progreso_paciente_fecha UNIQUE (id_paciente, fecha),
  CHECK (peso_kg BETWEEN 20 AND 300)
) ENGINE=InnoDB;
