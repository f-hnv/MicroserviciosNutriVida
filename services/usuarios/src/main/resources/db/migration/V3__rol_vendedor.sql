-- Agrega el rol VENDEDOR a los valores permitidos de usuario.rol.
-- La restricción original no tenía nombre, por lo que MySQL la llamó usuario_chk_1.
ALTER TABLE usuario DROP CHECK usuario_chk_1;
ALTER TABLE usuario ADD CONSTRAINT ck_usuario_rol
  CHECK (rol IN ('PACIENTE','MEDICO','ADMIN','VENDEDOR'));
