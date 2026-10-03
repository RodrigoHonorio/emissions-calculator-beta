package uk.org.spire.emissions_calculator_beta.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uk.org.spire.emissions_calculator_beta.entity.AirQualityMeasurement;
import uk.org.spire.emissions_calculator_beta.entity.AirQualityStation;
import uk.org.spire.emissions_calculator_beta.repository.AirQualityMeasurementRepository;
import uk.org.spire.emissions_calculator_beta.repository.AirQualityStationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirQualityBackfillService {

    private final AirQualityStationRepository stationRepository;
    private final AirQualityMeasurementRepository measurementRepository;
    private final RestTemplate restTemplate;
    private final BackfillProgressTrackerService progressTrackerService;

    private static final DateTimeFormatter API_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter API_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Inicia a recuperação automática de lacunas (gaps) para todas as estações ativas.
     * Recupera no máximo os últimos 7 dias para evitar sobrecarga na API externa.
     */
    public void runAutomaticBackfill() {
        log.info("🔍 Iniciando rotina de verificação e recuperação (Backfill) de gaps de dados...");

        List<AirQualityStation> activeStations = stationRepository.findAll().stream()
                .filter(s -> s.getIsActive() != null && s.getIsActive())
                .toList();

        // Inicia o rastreio do progresso com o total de estações ativas
        progressTrackerService.start(activeStations.size());

        // Pré-carrega o mapa com a última medição de cada estação numa única consulta (Elimina o problema N+1)
        Map<Long, LocalDateTime> latestTimestampsMap = measurementRepository.findLatestTimestampsPerStation()
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (LocalDateTime) row[1]
                ));

        LocalDate today = LocalDate.now();
        LocalDate maxPastDate = today.minusDays(7); // Limite de 7 dias de retroatividade

        int totalRecovered = 0;
        int currentIndex = 0;

        for (AirQualityStation station : activeStations) {
            currentIndex++;
            progressTrackerService.update(
                    currentIndex,
                    String.format("A sincronizar: %s (%d/%d)", station.getName(), currentIndex, activeStations.size())
            );

            // Obtém a última medição diretamente do mapa em memória
            Optional<LocalDateTime> lastMeasurement = Optional.ofNullable(latestTimestampsMap.get(station.getId()));

            LocalDate startDate;

            if (lastMeasurement.isPresent()) {
                LocalDate lastDate = lastMeasurement.get().toLocalDate();
                if (lastDate.isEqual(today)) {
                    continue; // Estação está em dia, passa para a próxima
                }
                startDate = lastDate.isBefore(maxPastDate) ? maxPastDate : lastDate;
            } else {
                startDate = maxPastDate;
            }

            try {
                int recovered = backfillGapsForStation(station, startDate, today);
                totalRecovered += recovered;

                Thread.sleep(500); // Pausa para respeitar o Rate Limit da API
            } catch (Exception e) {
                log.error("❌ Erro ao tentar recuperar dados (backfill) para a estação {}: {}", station.getSiteCode(), e.getMessage());
            }
        }

        progressTrackerService.finish();
        log.info("✅ Rotina de Backfill concluída! Total de medições históricas recuperadas: {}", totalRecovered);
    }

    @Transactional
    public int backfillGapsForStation(AirQualityStation station, LocalDate startDate, LocalDate endDate) {
        String siteCode = station.getSiteCode();

        String url = String.format(
                "https://api.erg.ic.ac.uk/AirQuality/Data/Wide/Site/SiteCode=%s/StartDate=%s/EndDate=%s/Json",
                siteCode,
                startDate.format(API_DATE_FORMAT),
                endDate.format(API_DATE_FORMAT)
        );

        Map<String, Object> response;
        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            return 0;
        }

        if (response == null || !response.containsKey("AirQualityData")) {
            return 0;
        }

        Map<String, Object> aqData = (Map<String, Object>) response.get("AirQualityData");
        if (aqData.containsKey("Data")) {
            Object dataObj = aqData.get("Data");
            List<Map<String, Object>> records = new ArrayList<>();

            if (dataObj instanceof List) {
                records = (List<Map<String, Object>>) dataObj;
            } else if (dataObj instanceof Map) {
                records.add((Map<String, Object>) dataObj);
            }

            List<AirQualityMeasurement> measurementsToSave = new ArrayList<>();

            for (Map<String, Object> rec : records) {
                String dateStr = (String) rec.get("@MeasurementDateGMT");
                if (dateStr == null) continue;

                LocalDateTime timestamp;
                try {
                    timestamp = LocalDateTime.parse(dateStr, API_TIMESTAMP_FORMAT);
                } catch (Exception e) {
                    continue;
                }

                if (measurementRepository.existsByStationAndMeasuredAt(station, timestamp)) {
                    continue;
                }

                AirQualityMeasurement m = AirQualityMeasurement.builder()
                        .station(station)
                        .measuredAt(timestamp)
                        .no2(parsePollutantValue(rec.get("@NO2")))
                        .pm10(parsePollutantValue(rec.get("@PM10")))
                        .pm25(parsePollutantValue(rec.get("@PM25")))
                        .o3(parsePollutantValue(rec.get("@O3")))
                        .build();

                if (m.getNo2() != null || m.getPm10() != null || m.getPm25() != null || m.getO3() != null) {
                    measurementsToSave.add(m);
                }
            }

            if (!measurementsToSave.isEmpty()) {
                measurementRepository.saveAll(measurementsToSave);
                return measurementsToSave.size();
            }
        }

        return 0;
    }

    private Double parsePollutantValue(Object val) {
        if (val == null || val.toString().trim().isEmpty()) return null;
        try {
            return Double.parseDouble(val.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}