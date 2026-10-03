package uk.org.spire.emissions_calculator_beta.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "station_daily_emissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"daily_snapshot_id", "gas_station_id"}, name = "uk_snapshot_gas_station")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationDailyEmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "daily_snapshot_id", nullable = false)
    private DailySnapshot dailySnapshot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gas_station_id", nullable = false)
    private GasStation gasStation;

    @Column(name = "calculated_evaporation_grams", nullable = false)
    private Double calculatedEvaporationGrams;

    @Column(name = "applied_emission_factor", nullable = false)
    private Double appliedEmissionFactor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}