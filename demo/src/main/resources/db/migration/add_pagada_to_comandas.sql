-- Script para agregar la columna 'pagada' a la tabla restaurante_comandas
-- Ejecutar este script en la base de datos MySQL

ALTER TABLE restaurante_comandas 
ADD COLUMN pagada BOOLEAN NOT NULL DEFAULT FALSE 
AFTER estado;

-- Actualizar comandas existentes para que todas tengan pagada = false por defecto
UPDATE restaurante_comandas 
SET pagada = FALSE 
WHERE pagada IS NULL;

