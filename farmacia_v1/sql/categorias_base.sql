-- Script para insertar categorías base en la tabla 'categoria'
-- Sincronizado con la siembra inicial de V1Application y la constante CATEGORIAS_FARMACIA del frontend

INSERT INTO categoria (nombre, descripcion) VALUES ('Antibióticos', 'Medicamentos antibacterianos');
INSERT INTO categoria (nombre, descripcion) VALUES ('Analgésicos', 'Alivio del dolor e inflamación');
INSERT INTO categoria (nombre, descripcion) VALUES ('Equipo Médico', 'Dispositivos e instrumental médico');
INSERT INTO categoria (nombre, descripcion) VALUES ('Cuidado Personal', 'Higiene y primeros auxilios');
