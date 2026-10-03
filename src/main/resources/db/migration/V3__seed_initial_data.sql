-- =========================================================================
-- SCRIPT DE CARGA INICIAL (SEED DATA) - GRANDE LONDRES
-- =========================================================================

-- 1. Inserir um Distrito de Exemplo (LondonBorough) - Ex: City of Westminster (Simplificado)
INSERT INTO london_boroughs (name, code, boundary, created_at, updated_at)
VALUES (
    'City of Westminster',
    'WES',
    ST_Multi(ST_GeomFromText('POLYGON((-0.19 51.53, -0.11 51.53, -0.11 51.49, -0.19 51.49, -0.19 51.53))', 4326)),
    NOW(),
    NOW()
) ON CONFLICT (code) DO NOTHING;


-- 2. Inserir Postos de Combustível de Exemplo (GasStation)
INSERT INTO gas_stations (external_id, name, operator, address, postcode, geometry, fuel_throughput_litres_per_year, stage_two_vr_active, data_hash, created_at, updated_at)
VALUES
(
    'GS-LON-001',
    'Shell Piccadilly Circus',
    'Shell',
    'Regent St, St. James s, London',
    'W1S 4LZ',
    ST_SetSRID(ST_MakePoint(-0.1340, 51.5098), 4326),
    2500000.0,
    true,
    'hash_sample_01',
    NOW(),
    NOW()
),
(
    'GS-LON-002',
    'BP Park Lane',
    'BP',
    'Park Lane, Mayfair, London',
    'W1K 1BE',
    ST_SetSRID(ST_MakePoint(-0.1512, 51.5074), 4326),
    3100000.0,
    true,
    'hash_sample_02',
    NOW(),
    NOW()
)
ON CONFLICT (external_id) DO NOTHING;


-- 3. Inserir Estações de Monitorização da Qualidade do Ar (AirQualityStation / MonitoringStation)
-- Nota: Adaptado para a tabela correspondente no esquema atual
INSERT INTO air_quality_stations (external_id, site_code, name, site_type, geometry, is_active, data_hash, created_at, updated_at)
VALUES
(
    'AQ-LAQN-WM6',
    'WM6',
    'Westminster - Marylebone Road',
    'Roadside',
    ST_SetSRID(ST_MakePoint(-0.1545, 51.5225), 4326),
    true,
    'hash_aq_01',
    NOW(),
    NOW()
),
(
    'AQ-LAQN-KC1',
    'KC1',
    'Kensington and Chelsea - North Kensington',
    'Urban Background',
    ST_SetSRID(ST_MakePoint(-0.2134, 51.5210), 4326),
    true,
    'hash_aq_02',
    NOW(),
    NOW()
)
ON CONFLICT (external_id) DO NOTHING;