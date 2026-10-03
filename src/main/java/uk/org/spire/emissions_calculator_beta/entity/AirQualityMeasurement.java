package uk.org.spire.emissions_calculator_beta.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "air_quality_measurements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirQualityMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private AirQualityStation station;

    @Column(name = "measurement_timestamp", nullable = false)
    private LocalDateTime measuredAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // <--- CAMPO ADICIONADO AQUI

    @Column(name = "no2_ug_m3")
    private Double no2;

    @Column(name = "pm10_ug_m3")
    private Double pm10;

    @Column(name = "pm25_ug_m3")
    private Double pm25;

    @Column(name = "o3_ug_m3")
    private Double o3;

    @Column(name = "aqi_index")
    private Integer aqiIndex;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}