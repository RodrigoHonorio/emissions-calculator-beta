package uk.org.spire.emissions_calculator_beta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StationApiResponse {

    @JsonProperty("Sites")
    private SitesContainerDto sites;
}