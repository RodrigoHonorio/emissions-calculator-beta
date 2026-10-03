package uk.org.spire.emissions_calculator_beta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SpeciesDataDto {

    @JsonProperty("@SpeciesCode")
    private String speciesCode; // ex: NO2, PM10, PM25, O3

    @JsonProperty("@AirQualityIndex")
    private Integer airQualityIndex;

    @JsonProperty("@AirQualityBand")
    private String airQualityBand;
}