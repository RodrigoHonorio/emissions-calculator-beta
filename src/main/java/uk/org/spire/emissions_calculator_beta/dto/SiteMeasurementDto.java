package uk.org.spire.emissions_calculator_beta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SiteMeasurementDto {

    @JsonProperty("@SiteCode")
    private String siteCode;

    @JsonProperty("Species")
    private List<SpeciesDataDto> speciesList;
}