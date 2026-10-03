package uk.org.spire.emissions_calculator_beta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SiteDto {

    @JsonProperty("@SiteCode")
    private String siteCode;

    @JsonProperty("@SiteName")
    private String siteName;

    @JsonProperty("@SiteType")
    private String siteType;

    @JsonProperty("@Latitude")
    private Double latitude;

    @JsonProperty("@Longitude")
    private Double longitude;
}