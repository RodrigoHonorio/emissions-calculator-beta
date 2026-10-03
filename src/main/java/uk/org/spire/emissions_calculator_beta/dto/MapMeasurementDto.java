package uk.org.spire.emissions_calculator_beta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapMeasurementDto {
    private String pollutant;
    private Double value;
    private String unit;
}