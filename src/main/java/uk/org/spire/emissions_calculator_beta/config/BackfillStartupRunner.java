package uk.org.spire.emissions_calculator_beta.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.org.spire.emissions_calculator_beta.service.AirQualityBackfillService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackfillStartupRunner implements CommandLineRunner {

    private final AirQualityBackfillService backfillService;

    @Override
    public void run(String... args) {
        log.info("⏳ A preparar rotina de Backfill em segundo plano...");

        // Executa numa nova Thread para não bloquear o arranque da API/Servidor Web
        new Thread(() -> {
            backfillService.runAutomaticBackfill();
        }).start();
    }
}