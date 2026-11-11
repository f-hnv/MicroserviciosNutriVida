ALTER TABLE pedido ADD COLUMN guest_token_hash CHAR(64) NULL;
ALTER TABLE pedido ADD COLUMN solicitud_hash CHAR(64) NULL;
