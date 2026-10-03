package uk.org.spire.emissions_calculator_beta.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "london_boroughs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LondonBorough {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    // Impede o Jackson de tentar ler os atributos internos do MultiPolygon
    @JsonIgnore
    @Column(nullable = false, columnDefinition = "GEOMETRY(MultiPolygon, 4326)")
    private MultiPolygon boundary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Serializa a geometria diretamente para a chave "boundary" do JSON
    @JsonProperty("boundary")
    @JsonRawValue
    public String getBoundaryGeoJson() {
        if (this.boundary == null) {
            return null;
        }
        try {
            GeoJsonWriter writer = new GeoJsonWriter();
            return writer.write(this.boundary);
        } catch (Exception e) {
            return null;
        }
    }

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