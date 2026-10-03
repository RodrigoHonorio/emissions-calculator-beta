package uk.org.spire.emissions_calculator_beta.service;

import uk.org.spire.emissions_calculator_beta.dto.MapItemResponseDTO;
import uk.org.spire.emissions_calculator_beta.dto.MapMeasurementDto;
import uk.org.spire.emissions_calculator_beta.entity.AirQualityMeasurement;
import uk.org.spire.emissions_calculator_beta.entity.AirQualityStation;
import uk.org.spire.emissions_calculator_beta.repository.AirQualityStationRepository;
import uk.org.spire.emissions_calculator_beta.service.scientific.AirQualityLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AirQualityMapService {

    private final AirQualityStationRepository stationRepository;

    public AirQualityMapService(AirQualityStationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public List<MapItemResponseDTO> getAirStationsForMap() {
        List<AirQualityStation> stations = stationRepository.findAll();

        return stations.stream()
                .map(this::convertToMapItemDto)
                .collect(Collectors.toList());
    }

    private MapItemResponseDTO convertToMapItemDto(AirQualityStation station) {
        // 1. Procura a medição mais recente que contenha um índice AQI
        Optional<AirQualityMeasurement> latestMeasurementWithAqi = (station.getMeasurements() == null)
                ? Optional.empty()
                : station.getMeasurements().stream()
                .filter(m -> Objects.nonNull(m.getAqiIndex()))
                .max(Comparator.comparing(AirQualityMeasurement::getMeasuredAt));

        // 2. Extrai o índice AQI
        Integer aqiIndex = latestMeasurementWithAqi.map(AirQualityMeasurement::getAqiIndex).orElse(null);

        // 3. Converte o AQI para o Enum AirQualityLevel via método estático fromIndex
        AirQualityLevel level = (aqiIndex != null) ? AirQualityLevel.fromIndex(aqiIndex) : null;
        String description = (level != null) ? level.getDescription() : null;
        String hexColor = (level != null) ? level.getHexColor() : null;

        // 4. Monta o DTO pronto para o mapa
        MapItemResponseDTO dto = new MapItemResponseDTO();
        dto.setId(station.getId());
        dto.setName(station.getName());
        dto.setType("AIR_STATION");
        dto.setSubtitle(station.getSiteCode());
        dto.setDetails(station.getSiteType());
        dto.setIsActive(station.getIsActive());

        if (station.getGeometry() != null) {
            dto.setCoordinates(new double[]{
                    station.getGeometry().getX(), // Longitude
                    station.getGeometry().getY()  // Latitude
            });
        }

        // 5. Mapeamento da lista de medições
        if (station.getMeasurements() != null && !station.getMeasurements().isEmpty()) {
            List<MapMeasurementDto> measurementDTOs = station.getMeasurements().stream()
                    .filter(m -> m.getAqiIndex() != null)
                    .max(Comparator.comparing(AirQualityMeasurement::getMeasuredAt))
                    .map(this::extractMeasurementList)
                    .orElse(List.of());

            dto.setMeasurements(measurementDTOs);
        }

        // 6. Injeta a classificação oficial
        dto.setAirQualityDescription(description);
        dto.setHexColor(hexColor);

        return dto;
    }

    private List<MapMeasurementDto> extractMeasurementList(AirQualityMeasurement measurement) {
        List<MapMeasurementDto> list = new ArrayList<>();

        if (measurement.getNo2() != null) {
            list.add(new MapMeasurementDto("NO2", measurement.getNo2(), "µg/m³"));
        }
        if (measurement.getPm10() != null) {
            list.add(new MapMeasurementDto("PM10", measurement.getPm10(), "µg/m³"));
        }
        if (measurement.getPm25() != null) {
            list.add(new MapMeasurementDto("PM2.5", measurement.getPm25(), "µg/m³"));
        }
        if (measurement.getO3() != null) {
            list.add(new MapMeasurementDto("O3", measurement.getO3(), "µg/m³"));
        }
        if (measurement.getAqiIndex() != null) {
            list.add(new MapMeasurementDto("AQI (DAQI)", measurement.getAqiIndex().doubleValue(), "Index"));
        }

        return list;
    }
}