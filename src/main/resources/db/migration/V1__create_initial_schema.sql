-- Habilita a extensão PostGIS para tipos e índices geográficos
CREATE EXTENSION IF NOT EXISTS postgis;

-- 1. Tabela de Postos de Combustível (GasStation)
CREATE TABLE gas_stations (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(100) UNIQUE,
    name VARCHAR(255) NOT NULL,
    operator VARCHAR(150),
    address TEXT,
    postcode VARCHAR(20),
    geometry GEOMETRY(Point, 4326) NOT NULL, -- Coordenadas WGS84 (Lon, Lat)
    fuel_throughput_litres_per_year DOUBLE PRECISION, -- Volume anual estimado
    stage_two_vr_active BOOLEAN DEFAULT TRUE, -- Sistema de recuperação de vapores
    data_hash VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_synced_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0
);

-- Índice espacial PostGIS para buscas geográficas ultrarrápidas
CREATE INDEX idx_gas_stations_geometry ON gas_stations USING GIST(geometry);
CREATE INDEX idx_gas_stations_external_id ON gas_stations(external_id);

-- 2. Tabela de Estações da Rede LAQN (AirQualityStation)
CREATE TABLE air_quality_stations (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(100) UNIQUE NOT NULL,
    site_code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    site_type VARCHAR(100), -- ex: Roadside, Urban Background
    geometry GEOMETRY(Point, 4326) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    data_hash VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_synced_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_air_quality_stations_geometry ON air_quality_stations USING GIST(geometry);

-- 3. Tabela de Leituras de Qualidade do Ar (AirQualityMeasurement)
CREATE TABLE air_quality_measurements (
    id BIGSERIAL PRIMARY KEY,
    station_id BIGINT NOT NULL REFERENCES air_quality_stations(id) ON DELETE CASCADE,
    measurement_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    no2_ug_m3 DOUBLE PRECISION,
    pm10_ug_m3 DOUBLE PRECISION,
    voc_ppb DOUBLE PRECISION,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_station_timestamp UNIQUE (station_id, measurement_timestamp)
);

CREATE INDEX idx_aq_measurements_timestamp ON air_quality_measurements(measurement_timestamp);

-- 4. Tabela de Leituras Meteorológicas (MeteorologicalMeasurement)
CREATE TABLE meteorological_measurements (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(100),
    geometry GEOMETRY(Point, 4326) NOT NULL, -- Ponto de amostragem da Open-Meteo
    observation_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    temperature_celsius DOUBLE PRECISION NOT NULL,
    wind_speed_m_s DOUBLE PRECISION NOT NULL,
    wind_direction_degrees DOUBLE PRECISION NOT NULL,
    solar_radiation_w_m2 DOUBLE PRECISION,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_synced_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_geo_timestamp UNIQUE (geometry, observation_timestamp)
);

CREATE INDEX idx_meteo_geometry ON meteorological_measurements USING GIST(geometry);
CREATE INDEX idx_meteo_timestamp ON meteorological_measurements(observation_timestamp);