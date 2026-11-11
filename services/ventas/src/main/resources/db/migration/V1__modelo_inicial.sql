CREATE TABLE pedido (
  id_pedido CHAR(36) NOT NULL,
  clave_idempotencia VARCHAR(64) NOT NULL,
  nombre_cliente VARCHAR(120) NOT NULL,
  correo_cliente VARCHAR(254) NOT NULL,
  telefono_cliente VARCHAR(20) NOT NULL,
  direccion_cliente VARCHAR(250) NOT NULL,
  region_cliente VARCHAR(80) NOT NULL,
  comuna_cliente VARCHAR(80) NOT NULL,
  modalidad VARCHAR(16) NOT NULL,
  total DECIMAL(12,2) NOT NULL,
  estado_pago VARCHAR(16) NOT NULL,
  metodo_pago VARCHAR(32) NOT NULL,
  fecha_creacion DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  id_usuario CHAR(36) NULL,
  CONSTRAINT pk_pedido PRIMARY KEY (id_pedido),
  CONSTRAINT uq_pedido_idempotencia UNIQUE (clave_idempotencia),
  CHECK (total >= 0),
  CHECK (estado_pago IN ('PENDIENTE','PAGADA','RECHAZADA')),
  CHECK (modalidad IN ('PRESENCIAL','ONLINE'))
) ENGINE=InnoDB;
CREATE INDEX ix_pedido_1 ON pedido (id_usuario, fecha_creacion);

CREATE TABLE detalle_pedido (
  id_detalle_pedido CHAR(36) NOT NULL,
  titulo_servicio VARCHAR(150) NOT NULL,
  cantidad SMALLINT NOT NULL,
  precio_base DECIMAL(12,2) NOT NULL,
  descuento_porcentaje DECIMAL(5,2) NOT NULL DEFAULT 0,
  precio_unitario DECIMAL(12,2) NOT NULL,
  id_pedido CHAR(36) NOT NULL,
  id_servicio CHAR(36) NOT NULL,
  CONSTRAINT pk_detalle_pedido PRIMARY KEY (id_detalle_pedido),
  CONSTRAINT fk_detalle_pedido_id_pedido FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido) ON DELETE RESTRICT,
  CONSTRAINT uq_detalle_servicio UNIQUE (id_pedido, id_servicio),
  CHECK (cantidad BETWEEN 1 AND 10),
  CHECK (precio_base >= 0 AND precio_unitario >= 0 AND precio_unitario <= precio_base),
  CHECK (descuento_porcentaje BETWEEN 0 AND 90)
) ENGINE=InnoDB;
CREATE INDEX ix_detalle_pedido_1 ON detalle_pedido (id_servicio);
