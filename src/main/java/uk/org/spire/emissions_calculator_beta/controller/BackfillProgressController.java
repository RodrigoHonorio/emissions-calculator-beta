package uk.org.spire.emissions_calculator_beta.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.org.spire.emissions_calculator_beta.dto.BackfillProgressResponse;
import uk.org.spire.emissions_calculator_beta.service.AirQualityBackfillService;
import uk.org.spire.emissions_calculator_beta.service.BackfillProgressTrackerService;

@RestController
@RequestMapping("/api/backfill")
@RequiredArgsConstructor
public class BackfillProgressController {

    private final BackfillProgressTrackerService progressTrackerService;
    private final AirQualityBackfillService backfillService;

    @GetMapping("/progress")
    public ResponseEntity<BackfillProgressResponse> getProgress() {
        return ResponseEntity.ok(progressTrackerService.toResponse());
    }

    @GetMapping("/trigger")
    public ResponseEntity<String> triggerBackfill() {
        // Executa em uma nova thread para não bloquear a resposta HTTP
        new Thread(() -> backfillService.runAutomaticBackfill()).start();
        return ResponseEntity.ok("Backfill disparado manualmente com sucesso! Volte ao dashboard para ver a barra.");
    }
}