package uk.org.spire.emissions_calculator_beta.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.org.spire.emissions_calculator_beta.dto.SiteMeasurementDto;
import uk.org.spire.emissions_calculator_beta.dto.SpeciesDataDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirQualitySyncSchedulerService {

    private final RestTemplate restTemplate;
    private final AirQualityMeasurementSyncService syncService;

    /**
     * Executa automaticamente a cada hora, ao minuto 15 (ex: 14:15, 15:15, 16:15...)
     * A margem de 15 minutos garante que a API da ERG já consolidou os dados da hora anterior.
     * Expressão Cron: "0 15 * * * *" -> (segundos minutos horas dia mês dia-da-semana)
     */
    @Scheduled(cron = "0 15 * * * *")
    public void scheduleAirQualitySync() {
        log.info("⏰ A iniciar sincronização automática agendada com a API do ERG...");
        try {
            fetchAndSync();
        } catch (Exception e) {
            log.error("❌ Falha na sincronização automática agendada: {}", e.getMessage(), e);
        }
    }

    /**
     * Método responsável por procurar os dados na API externa do ERG e chamar a persistência.
     * Pode ser invocado tanto pelo agendador quanto manualmente via Controller.
     */
    public void fetchAndSync() {
        String url = "https://api.erg.ic.ac.uk/AirQuality/Hourly/MonitoringIndex/GroupName=London/Json";

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null) {
            log.warn("⚠️ Resposta nula da API externa do ERG.");
            return;
        }

        Map<String, Object> rootIndex = null;
        if (response.containsKey("DailyAirQualityIndex")) {
            rootIndex = (Map<String, Object>) response.get("DailyAirQualityIndex");
        } else if (response.containsKey("HourlyAirQualityIndex")) {
            rootIndex = (Map<String, Object>) response.get("HourlyAirQualityIndex");
        }

        if (rootIndex != null && rootIndex.containsKey("LocalAuthority")) {
            Object localAuthObj = rootIndex.get("LocalAuthority");
            List<Map<String, Object>> localAuthorities = new ArrayList<>();

            if (localAuthObj instanceof List) {
                localAuthorities = (List<Map<String, Object>>) localAuthObj;
            } else if (localAuthObj instanceof Map) {
                localAuthorities.add((Map<String, Object>) localAuthObj);
            }

            List<SiteMeasurementDto> measurements = new ArrayList<>();

            for (Map<String, Object> la : localAuthorities) {
                if (la.containsKey("Site")) {
                    Object siteObj = la.get("Site");
                    List<Map<String, Object>> siteList = new ArrayList<>();

                    if (siteObj instanceof List) {
                        siteList = (List<Map<String, Object>>) siteObj;
                    } else if (siteObj instanceof Map) {
                        siteList.add((Map<String, Object>) siteObj);
                    }

                    for (Map<String, Object> item : siteList) {
                        SiteMeasurementDto dto = new SiteMeasurementDto();
                        dto.setSiteCode((String) item.get("@SiteCode"));

                        Object speciesObj = item.get("Species");
                        List<SpeciesDataDto> speciesDtos = new ArrayList<>();

                        if (speciesObj instanceof List) {
                            for (Object spObj : (List<?>) speciesObj) {
                                if (spObj instanceof Map) {
                                    speciesDtos.add(parseSpecies((Map<String, Object>) spObj));
                                }
                            }
                        } else if (speciesObj instanceof Map) {
                            speciesDtos.add(parseSpecies((Map<String, Object>) speciesObj));
                        }

                        dto.setSpeciesList(speciesDtos);
                        measurements.add(dto);
                    }
                }
            }
            syncService.syncMeasurements(measurements);
        }
    }

    private SpeciesDataDto parseSpecies(Map<String, Object> sp) {
        SpeciesDataDto spDto = new SpeciesDataDto();
        spDto.setSpeciesCode((String) sp.get("@SpeciesCode"));
        Object indexVal = sp.get("@AirQualityIndex");
        if (indexVal != null) {
            try {
                spDto.setAirQualityIndex(Integer.parseInt(indexVal.toString()));
            } catch (NumberFormatException ignored) {}
        }
        return spDto;
    }
}