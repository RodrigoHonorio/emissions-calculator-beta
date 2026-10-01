package uk.org.spire.emissions_calculator_beta.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "gas_stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GasStation extends BaseSyncEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String operator;

    private String address;

    private String postcode;

    @Column(nullable = false, columnDefinition = "GEOMETRY(Point, 4326)")
    private Point geometry;

    @Column(name = "fuel_throughput_litres_per_year")
    private Double fuelThroughputLitresPerYear;

    @Builder.Default
    @Column(name = "stage_two_vr_active")
    private Boolean stageTwoVrActive = true;
}
