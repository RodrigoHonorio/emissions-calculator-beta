package uk.org.spire.emissions_calculator_beta.service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uk.org.spire.emissions_calculator_beta.dto.SiteDto;
import uk.org.spire.emissions_calculator_beta.dto.StationApiResponse;
import uk.org.spire.emissions_calculator_beta.entity.AirQualityStation;
import uk.org.spire.emissions_calculator_beta.repository.AirQualityStationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AirQualityStationSyncService {

    private final AirQualityStationRepository repository;
    private final RestTemplate restTemplate;
    private final String apiUrl;

    public AirQualityStationSyncService(
            AirQualityStationRepository repository,
            RestTemplate restTemplate,
            @Value("${tfl.api.url:https://api.erg.ic.ac.uk/AirQuality/Information/MonitoringSites/GroupName=London/Json}") String apiUrl) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    /**
     * Método chamado pelo MasterDataSeeder para orquestrar a carga das estações de forma otimizada
     */
    @Transactional
    public void sync() {
        try {
            System.out.println(">>> A conectar à API externa para descarregar as estações de Londres...");

            StationApiResponse response = restTemplate.getForObject(apiUrl, StationApiResponse.class);

            if (response != null && response.getSites() != null && response.getSites().getSiteList() != null) {
                List<SiteDto> siteDtos = response.getSites().getSiteList();

                // 1. Extrair todos os códigos de site válidos que vêm da API
                List<String> incomingSiteCodes = siteDtos.stream()
                        .filter(dto -> dto.getSiteCode() != null && dto.getLatitude() != null && dto.getLongitude() != null)
                        .map(SiteDto::getSiteCode)
                        .distinct()
                        .collect(Collectors.toList());

                if (incomingSiteCodes.isEmpty()) {
                    System.out.println(">>> Nenhuma estação válida encontrada na resposta da API.");
                    return;
                }

                // 2. Executar APENAS UMA consulta à base de dados para verificar quais já existem
                List<AirQualityStation> existingStations = repository.findBySiteCodeIn(incomingSiteCodes); //
                Set<String> existingSiteCodes = existingStations.stream()
                        .map(AirQualityStation::getSiteCode)
                        .collect(Collectors.toSet());

                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                List<AirQualityStation> stationsToSave = new ArrayList<>();
                int countSkipped = 0;

                // 3. Processar os dados em memória iterando pelos DTOs sem novas queries individuais
                for (SiteDto dto : siteDtos) {
                    if (dto.getSiteCode() != null && dto.getLatitude() != null && dto.getLongitude() != null) {

                        // Verifica rapidamente no Set em memória O(1) se já existe
                        if (!existingSiteCodes.contains(dto.getSiteCode())) {
                            String externalId = "AQ-LAQN-" + dto.getSiteCode();

                            AirQualityStation station = AirQualityStation.builder()
                                    .siteCode(dto.getSiteCode())
                                    .name(dto.getSiteName())
                                    .siteType(dto.getSiteType())
                                    .geometry(geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude())))
                                    .isActive(true)
                                    .build();

                            station.setExternalId(externalId);
                            stationsToSave.add(station);

                            // Adiciona ao set local para evitar duplicados caso a API traga entradas repetidas
                            existingSiteCodes.add(dto.getSiteCode());
                        } else {
                            countSkipped++;
                        }
                    }
                }

                // 4. Gravar todas as novas estações de uma só vez utilizando saveAll (Batch Insert)
                if (!stationsToSave.isEmpty()) {
                    repository.saveAll(stationsToSave);
                }

                System.out.println(">>> Sincronização concluída! " + stationsToSave.size() + " novas estações guardadas, " + countSkipped + " já existentes.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Falha ao sincronizar estações da API externa: " + e.getMessage());
        }
    }
}