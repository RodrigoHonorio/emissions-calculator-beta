-- 1. Tabela de Distritos de Londres (LondonBorough)
CREATE TABLE london_boroughs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    boundary GEOMETRY(MultiPolygon, 4326) NOT NULL, -- Limites territoriais em Polígono/MultiPolígono
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_boroughs_boundary ON london_boroughs USING GIST(boundary);

-- 2. Tabela de Retrato do Dia (DailySnapshot)
CREATE TABLE daily_snapshots (
    id BIGSERIAL PRIMARY KEY,
    snapshot_date DATE UNIQUE NOT NULL,
    total_voc_emitted_kg DOUBLE PRECISION NOT NULL,
    avg_temperature DOUBLE PRECISION,
    dominant_wind_direction DOUBLE PRECISION,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_daily_snapshots_date ON daily_snapshots(snapshot_date);

-- 3. Tabela de Emissão Diária por Posto (StationDailyEmission)
CREATE TABLE station_daily_emissions (
    id BIGSERIAL PRIMARY KEY,
    daily_snapshot_id BIGINT NOT NULL REFERENCES daily_snapshots(id) ON DELETE CASCADE,
    gas_station_id BIGINT NOT NULL REFERENCES gas_stations(id) ON DELETE CASCADE,
    calculated_evaporation_grams DOUBLE PRECISION NOT NULL,
    applied_emission_factor DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_snapshot_gas_station UNIQUE (daily_snapshot_id, gas_station_id)
);

CREATE INDEX idx_station_emissions_snapshot ON station_daily_emissions(daily_snapshot_id);

-- 4. Tabela de Registos de Pontos Críticos (HotspotRecord)
CREATE TABLE hotspot_records (
    id BIGSERIAL PRIMARY KEY,
    daily_snapshot_id BIGINT NOT NULL REFERENCES daily_snapshots(id) ON DELETE CASCADE,
    location GEOMETRY(Point, 4326) NOT NULL,
    total_voc_concentration DOUBLE PRECISION NOT NULL,
    baseline_contribution DOUBLE PRECISION NOT NULL,
    stations_delta_contribution DOUBLE PRECISION NOT NULL,
    severity_level VARCHAR(30) NOT NULL, -- CRITICAL, HIGH, MODERATE
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_hotspots_geometry ON hotspot_records USING GIST(location);
CREATE INDEX idx_hotspots_snapshot ON hotspot_records(daily_snapshot_id);