package uk.org.spire.emissions_calculator_beta.dto;

public record BackfillProgressResponse(
        boolean running,
        int totalStations,
        int processedStations,
        String currentMessage,
        int percentage
) {}