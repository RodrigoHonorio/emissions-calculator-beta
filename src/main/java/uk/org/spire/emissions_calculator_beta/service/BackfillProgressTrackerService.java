package uk.org.spire.emissions_calculator_beta.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import uk.org.spire.emissions_calculator_beta.dto.BackfillProgressResponse;

@Getter
@Service
public class BackfillProgressTrackerService {
    private boolean running = false;
    private int totalStations = 0;
    private int processedStations = 0;
    private String currentMessage = "A aguardar início...";

    public synchronized void start(int total) {
        this.running = true;
        this.totalStations = total;
        this.processedStations = 0;
        this.currentMessage = "A iniciar sincronização histórica...";
    }

    public synchronized void update(int processed, String message) {
        this.processedStations = processed;
        this.currentMessage = message;
    }

    public synchronized void finish() {
        this.running = false;
        this.currentMessage = "Concluído!";
    }

    public int getPercentage() {
        if (totalStations == 0) return 0;
        return (processedStations * 100) / totalStations;
    }

    /**
     * Converte o estado atual num DTO seguro para exposição externa.
     */
    public synchronized BackfillProgressResponse toResponse() {
        return new BackfillProgressResponse(
                running,
                totalStations,
                processedStations,
                currentMessage,
                getPercentage()
        );
    }
}