CREATE TABLE sesion_refresh (
  id_sesion CHAR(36) NOT NULL PRIMARY KEY,
  id_usuario CHAR(36) NOT NULL,
  token_hash CHAR(64) NOT NULL UNIQUE,
  vence_en DATETIME(6) NOT NULL,
  CONSTRAINT fk_sesion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
) ENGINE=InnoDB;
