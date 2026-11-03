INSERT INTO listas (nombre, descripcion) VALUES ('Lista por defecto', 'Lista principal creada automáticamente');
UPDATE favoritos SET lista_id = (SELECT id FROM listas LIMIT 1) WHERE lista_id IS NULL;
ALTER TABLE favoritos ALTER COLUMN lista_id SET NOT NULL;
