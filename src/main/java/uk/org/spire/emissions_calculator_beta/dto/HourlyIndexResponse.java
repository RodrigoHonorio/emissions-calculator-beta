package uk.org.spire.emissions_calculator_beta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HourlyIndexResponse {

    @JsonProperty("DailyAirQualityIndex")
    private DailyAirQualityIndex dailyAirQualityIndex;

    @Data
    public static class DailyAirQualityIndex {
        @JsonProperty("LocalAuthority")
        private List<LocalAuthorityDto> localAuthorities;
    }

    @Data
    public static class LocalAuthorityDto {
        @JsonProperty("Site")
        private List<SiteMeasurementDto> siteList;
    }
}