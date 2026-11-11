CREATE TABLE usuario (
  id_usuario CHAR(36) NOT NULL,
  nombres VARCHAR(100) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  correo VARCHAR(254) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  rol VARCHAR(24) NOT NULL,
  run VARCHAR(12) NOT NULL,
  telefono VARCHAR(20) NOT NULL,
  direccion VARCHAR(250) NOT NULL,
  region VARCHAR(80) NOT NULL,
  comuna VARCHAR(80) NOT NULL,
  activo BIT(1) NOT NULL DEFAULT b'1',
  fecha_creacion DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
  CONSTRAINT uq_usuario_correo UNIQUE (correo),
  CONSTRAINT uq_usuario_run UNIQUE (run),
  CHECK (rol IN ('PACIENTE','MEDICO','ADMIN'))
) ENGINE=InnoDB;
