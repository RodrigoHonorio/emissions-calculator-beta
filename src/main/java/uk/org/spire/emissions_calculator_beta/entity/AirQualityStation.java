package uk.org.spire.emissions_calculator_beta.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "air_quality_stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirQualityStation extends BaseSyncEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_code", nullable = false, length = 50)
    private String siteCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "site_type", length = 100)
    private String siteType;

    @Column(nullable = false, columnDefinition = "GEOMETRY(Point, 4326)")
    private Point geometry;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AirQualityMeasurement> measurements = new ArrayList<>();
}