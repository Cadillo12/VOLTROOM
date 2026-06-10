-- ====================================================================
-- SCRIPT DE CARGA DE DATOS PARA VOLTROOM
-- Base de datos: voltroom_db (PostgreSQL)
-- 
-- Este script inserta datos reales para que el módulo de Monitoreo
-- funcione correctamente con el simulador de sensores.
--
-- ORDEN DE EJECUCIÓN:
-- 1. Inmuebles
-- 2. Ambientes  
-- 3. Sensores
-- 4. Tarifa activa
--
-- NOTA: Las lecturas y alertas se generan automáticamente por el 
--       SensorSimuladorService cada 5 segundos.
-- ====================================================================

-- ┌────────────────────────────────────────────────────┐
-- │  1. INMUEBLES (Edificios / Propiedades)            │
-- └────────────────────────────────────────────────────┘
INSERT INTO inmueble (id, nombre, direccion, descripcion, activo, fecha_creacion, fecha_actualizacion)
VALUES 
(1, 'Edificio Central UTP', 'Av. Arequipa 265, Lima', 'Edificio principal de la universidad', true, NOW(), NOW()),
(2, 'Residencia Los Olivos', 'Jr. Los Pinos 412, Los Olivos', 'Residencia multifamiliar de 5 pisos', true, NOW(), NOW()),
(3, 'Oficinas VoltRoom', 'Av. Javier Prado 1550, San Isidro', 'Sede administrativa de la empresa', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Resetear secuencia para que el próximo ID sea correcto
SELECT setval('inmueble_id_seq', (SELECT COALESCE(MAX(id), 0) FROM inmueble));

-- ┌────────────────────────────────────────────────────┐
-- │  2. AMBIENTES (Habitaciones / Zonas)               │
-- └────────────────────────────────────────────────────┘
INSERT INTO ambiente (id, inmueble_id, nombre, tipo, piso, estado, descripcion, fecha_creacion, fecha_actualizacion)
VALUES 
-- Edificio Central UTP
(1, 1, 'Laboratorio de Cómputo A', 'LABORATORIO', 2, 'ACTIVO', 'Lab con 30 PCs y aire acondicionado', NOW(), NOW()),
(2, 1, 'Aula 301', 'AULA', 3, 'ACTIVO', 'Aula teórica con proyector', NOW(), NOW()),
(3, 1, 'Sala de Servidores', 'DATACENTER', 1, 'ACTIVO', 'Sala de servidores con UPS y climatización 24/7', NOW(), NOW()),

-- Residencia Los Olivos
(4, 2, 'Departamento 201', 'DEPARTAMENTO', 2, 'ACTIVO', 'Departamento familiar de 3 habitaciones', NOW(), NOW()),
(5, 2, 'Departamento 302', 'DEPARTAMENTO', 3, 'ACTIVO', 'Departamento de 2 habitaciones', NOW(), NOW()),
(6, 2, 'Área Común - Lobby', 'AREA_COMUN', 1, 'ACTIVO', 'Recepción e iluminación del lobby', NOW(), NOW()),

-- Oficinas VoltRoom
(7, 3, 'Oficina de Desarrollo', 'OFICINA', 4, 'ACTIVO', 'Área del equipo de desarrollo de software', NOW(), NOW()),
(8, 3, 'Sala de Reuniones', 'SALA', 4, 'ACTIVO', 'Sala de reuniones con pantalla y AC', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

SELECT setval('ambiente_id_seq', (SELECT COALESCE(MAX(id), 0) FROM ambiente));

-- ┌────────────────────────────────────────────────────┐
-- │  3. SENSORES (Medidores eléctricos)                │
-- └────────────────────────────────────────────────────┘
INSERT INTO sensor (id, ambiente_id, codigo, tipo_sensor, unidad_medida, estado, umbral_maximo_kwh, fecha_instalacion, observacion, fecha_creacion, fecha_actualizacion)
VALUES 
-- Edificio Central UTP
(1, 1, 'PZEM-LAB-A01', 'PZEM-004T', 'kWh', 'OPERATIVO', 500.00, '2025-01-15', 'Sensor principal del laboratorio de cómputo', NOW(), NOW()),
(2, 2, 'PZEM-AULA-301', 'PZEM-004T', 'kWh', 'OPERATIVO', 200.00, '2025-01-15', 'Sensor del aula 301 - iluminación y proyector', NOW(), NOW()),
(3, 3, 'SDM-SRV-01', 'SDM120', 'kWh', 'OPERATIVO', 1000.00, '2025-01-10', 'Sensor de la sala de servidores - alta carga', NOW(), NOW()),

-- Residencia Los Olivos
(4, 4, 'PZEM-DPTO-201', 'PZEM-004T', 'kWh', 'OPERATIVO', 300.00, '2025-02-01', 'Medidor del departamento 201', NOW(), NOW()),
(5, 5, 'PZEM-DPTO-302', 'PZEM-004T', 'kWh', 'OPERATIVO', 250.00, '2025-02-01', 'Medidor del departamento 302', NOW(), NOW()),
(6, 6, 'PZEM-LOBBY-01', 'PZEM-004T', 'kWh', 'OPERATIVO', 100.00, '2025-02-10', 'Sensor de iluminación del lobby', NOW(), NOW()),

-- Oficinas VoltRoom
(7, 7, 'SDM-DEV-01', 'SDM120', 'kWh', 'OPERATIVO', 400.00, '2025-03-01', 'Sensor de la oficina de desarrollo', NOW(), NOW()),
(8, 8, 'PZEM-MEET-01', 'PZEM-004T', 'kWh', 'OPERATIVO', 150.00, '2025-03-01', 'Sensor de la sala de reuniones', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

SELECT setval('sensor_id_seq', (SELECT COALESCE(MAX(id), 0) FROM sensor));

-- ┌────────────────────────────────────────────────────┐
-- │  4. TARIFA ELÉCTRICA ACTIVA                        │
-- └────────────────────────────────────────────────────┘
-- Tarifa BT5B residencial de Luz del Sur (Perú 2025)
INSERT INTO tarifa (id, nombre, precio_por_kwh, fecha_inicio, fecha_fin, activa, descripcion, fecha_creacion)
VALUES 
(1, 'BT5B Residencial - Luz del Sur', 0.7565, '2025-01-01', NULL, true, 'Tarifa eléctrica BT5B residencial vigente para Lima Metropolitana', NOW())
ON CONFLICT (id) DO NOTHING;

SELECT setval('tarifa_id_seq', (SELECT COALESCE(MAX(id), 0) FROM tarifa));

-- ====================================================================
-- ¡LISTO! 
-- Al iniciar el backend, el SensorSimuladorService generará lecturas
-- automáticas cada 5 segundos para los 8 sensores OPERATIVOS.
-- 
-- Datos que se generan automáticamente:
--   → Tabla "lectura": voltaje, amperaje, potencia, kWh por sensor
--   → Tabla "alerta": si un sensor detecta voltaje fuera de rango
--   → Tabla "consumo_resumen": resumen mensual por ambiente
-- ====================================================================
