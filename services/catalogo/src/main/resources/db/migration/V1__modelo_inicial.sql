CREATE TABLE categoria (
  id_categoria CHAR(36) NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  activo BIT(1) NOT NULL DEFAULT b'1',
  CONSTRAINT pk_categoria PRIMARY KEY (id_categoria),
  CONSTRAINT uq_categoria_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE servicio (
  id_servicio CHAR(36) NOT NULL,
  titulo VARCHAR(150) NOT NULL,
  descripcion VARCHAR(2000) NOT NULL,
  precio DECIMAL(12,2) NOT NULL,
  descuento_porcentaje DECIMAL(5,2) NOT NULL DEFAULT 0,
  modalidad VARCHAR(16) NOT NULL,
  duracion_minutos SMALLINT NOT NULL,
  imagen_url VARCHAR(1000) NULL,
  activo BIT(1) NOT NULL DEFAULT b'1',
  id_categoria CHAR(36) NOT NULL,
  id_profesional CHAR(36) NULL,
  CONSTRAINT pk_servicio PRIMARY KEY (id_servicio),
  CONSTRAINT fk_servicio_id_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria) ON DELETE RESTRICT,
  CHECK (precio >= 0),
  CHECK (descuento_porcentaje BETWEEN 0 AND 90),
  CHECK (duracion_minutos BETWEEN 1 AND 600),
  CHECK (modalidad IN ('PRESENCIAL','ONLINE'))
) ENGINE=InnoDB;
CREATE INDEX ix_servicio_1 ON servicio (id_profesional);
