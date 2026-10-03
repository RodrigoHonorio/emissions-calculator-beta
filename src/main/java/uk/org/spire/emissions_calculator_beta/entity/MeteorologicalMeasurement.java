package uk.org.spire.emissions_calculator_beta.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;

@Entity
@Table(name = "meteorological_measurements", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"geometry", "observation_timestamp"}, name = "uk_geo_timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeteorologicalMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", length = 100)
    private String externalId;

    @Column(nullable = false, columnDefinition = "GEOMETRY(Point, 4326)")
    private Point geometry;

    @Column(name = "observation_timestamp", nullable = false)
    private OffsetDateTime observationTimestamp;

    @Column(name = "temperature_celsius", nullable = false)
    private Double temperatureCelsius;

    @Column(name = "wind_speed_m_s", nullable = false)
    private Double windSpeedMps;

    @Column(name = "wind_direction_degrees", nullable = false)
    private Double windDirectionDegrees;

    @Column(name = "solar_radiation_w_m2")
    private Double solarRadiationWM2;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "last_synced_at")
    private OffsetDateTime lastSyncedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}