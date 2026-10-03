package uk.org.spire.emissions_calculator_beta.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.org.spire.emissions_calculator_beta.dto.SiteMeasurementDto;
import uk.org.spire.emissions_calculator_beta.dto.SpeciesDataDto;
import uk.org.spire.emissions_calculator_beta.entity.AirQualityMeasurement;
import uk.org.spire.emissions_calculator_beta.entity.AirQualityStation;
import uk.org.spire.emissions_calculator_beta.repository.AirQualityMeasurementRepository;
import uk.org.spire.emissions_calculator_beta.repository.AirQualityStationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirQualityMeasurementSyncService {

    private final AirQualityStationRepository stationRepository;
    private final AirQualityMeasurementRepository measurementRepository;

    @Transactional
    public void syncMeasurements(List<SiteMeasurementDto> apiMeasurements) {
        if (apiMeasurements == null || apiMeasurements.isEmpty()) {
            log.warn("⚠️ Nenhum dado de medição retornado pela API.");
            return;
        }

        // 1. Extrair e normalizar todos os códigos de estação únicos da API (remover espaços e converter para maiúsculas)
        List<String> siteCodes = apiMeasurements.stream()
                .map(SiteMeasurementDto::getSiteCode)
                .filter(code -> code != null && !code.trim().isEmpty())
                .map(code -> code.trim().toUpperCase())
                .distinct()
                .collect(Collectors.toList());

        if (siteCodes.isEmpty()) {
            log.warn("⚠️ Nenhum siteCode válido encontrado nos dados da API.");
            return;
        }

        // 2. Buscar todas as estações correspondentes na base de dados em lote
        List<AirQualityStation> existingStations = stationRepository.findBySiteCodeIn(siteCodes);

        // 3. Converter a lista num Map para pesquisa O(1) usando chave normalizada
        Map<String, AirQualityStation> stationMap = existingStations.stream()
                .collect(Collectors.toMap(
                        s -> s.getSiteCode().trim().toUpperCase(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        List<AirQualityMeasurement> measurementsToSave = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 4. Processar os dados em memória iterando pelos DTOs
        for (SiteMeasurementDto measurementDto : apiMeasurements) {
            String siteCode = measurementDto.getSiteCode();
            if (siteCode == null) {
                continue;
            }

            AirQualityStation station = stationMap.get(siteCode.trim().toUpperCase());
            if (station == null) {
                log.warn("⚠️ Estação com siteCode '{}' veio da API mas não está registrada na base de dados.", siteCode);
                continue;
            }

            AirQualityMeasurement measurement = AirQualityMeasurement.builder()
                    .station(station)
                    .measuredAt(now)
                    .build();

            boolean hasValues = false;

            // Mapear os poluentes da lista SpeciesDataDto para as colunas reais da entidade
            if (measurementDto.getSpeciesList() != null) {
                for (SpeciesDataDto species : measurementDto.getSpeciesList()) {
                    String speciesCode = species.getSpeciesCode();
                    Integer indexVal = species.getAirQualityIndex();

                    if (speciesCode == null || indexVal == null) {
                        continue;
                    }

                    Double val = indexVal.doubleValue();
                    hasValues = true;

                    // Preenche o campo correspondente com base no código do poluente
                    switch (speciesCode.toUpperCase().trim()) {
                        case "NO2":
                            measurement.setNo2(val);
                            break;
                        case "PM10":
                            measurement.setPm10(val);
                            break;
                        case "PM2.5":
                        case "PM25":
                            measurement.setPm25(val);
                            break;
                        case "O3":
                            measurement.setO3(val);
                            break;
                        default:
                            break;
                    }

                    // Define o índice geral de AQI com base no primeiro valor disponível
                    if (measurement.getAqiIndex() == null) {
                        measurement.setAqiIndex(indexVal);
                    }
                }
            }

            if (hasValues) {
                measurementsToSave.add(measurement);
            }
        }

        // 5. Gravar tudo de uma só vez na base de dados utilizando saveAll (Batch Insert)
        if (!measurementsToSave.isEmpty()) {
            measurementRepository.saveAll(measurementsToSave);
            log.info("✅ Sincronização de dados reais concluída com sucesso! {} medições guardadas em lote.", measurementsToSave.size());
        } else {
            log.info("ℹ️ Nenhuma nova medição válida gerada para salvamento.");
        }
    }
}