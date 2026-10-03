package uk.org.spire.emissions_calculator_beta.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import uk.org.spire.emissions_calculator_beta.constants.SeverityLevel;

import java.time.OffsetDateTime;

@Entity
@Table(name = "hotspot_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotspotRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "daily_snapshot_id", nullable = false)
    private DailySnapshot dailySnapshot;

    @Column(nullable = false, columnDefinition = "GEOMETRY(Point, 4326)")
    private Point location;

    @Column(name = "total_voc_concentration", nullable = false)
    private Double totalVocConcentration;

    @Column(name = "baseline_contribution", nullable = false)
    private Double baselineContribution;

    @Column(name = "stations_delta_contribution", nullable = false)
    private Double stationsDeltaContribution;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity_level", nullable = false, length = 30)
    private  SeverityLevel severityLevel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}