package uk.org.spire.emissions_calculator_beta.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.org.spire.emissions_calculator_beta.service.AirQualityStationSyncService;
import uk.org.spire.emissions_calculator_beta.service.LondonBoroughSyncService;

@Slf4j
@Component
@RequiredArgsConstructor
public class MasterDataSeeder implements CommandLineRunner {

    private final LondonBoroughSyncService boroughSyncService;
    private final AirQualityStationSyncService airStationSyncService;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Iniciando a orquestração de dados da base (Master Seeder)...");

        // 1. Carrega primeiro as fronteiras geográficas (distritos)
        boroughSyncService.sync();

        // 2. Carrega/sincroniza a infraestrutura de monitorização ambiental
        airStationSyncService.sync();

        // No futuro, para adicionar as Bombas de Gasolina, bastará adicionar aqui:
        // gasStationSyncService.sync();

        log.info("🏁 Orquestração de dados base concluída com sucesso!");
    }
}