package uk.org.spire.emissions_calculator_beta.controller;

import uk.org.spire.emissions_calculator_beta.dto.MapItemResponseDTO;
import uk.org.spire.emissions_calculator_beta.service.AirQualityMapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/map")
public class MapDataController {

    private final AirQualityMapService airQualityMapService;

    public MapDataController(AirQualityMapService airQualityMapService) {
        this.airQualityMapService = airQualityMapService;
    }

    @GetMapping("/air-stations")
    public ResponseEntity<List<MapItemResponseDTO>> getAirStations() {
        List<MapItemResponseDTO> stations = airQualityMapService.getAirStationsForMap();
        return ResponseEntity.ok(stations);
    }
}