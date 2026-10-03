-- Adiciona colunas extras de poluentes e o índice AQI na tabela de medições se não existirem
ALTER TABLE air_quality_measurements ADD COLUMN IF NOT EXISTS pm25_ug_m3 DOUBLE PRECISION;
ALTER TABLE air_quality_measurements ADD COLUMN IF NOT EXISTS o3_ug_m3 DOUBLE PRECISION;
ALTER TABLE air_quality_measurements ADD COLUMN IF NOT EXISTS aqi_index INTEGER;