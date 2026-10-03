package uk.org.spire.emissions_calculator_beta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapItemResponseDTO {
    private Long id;
    private String name;
    private String type;          // "GAS_STATION" ou "AIR_STATION"
    private String subtitle;      // Operador ou código da estação
    private String details;       // Morada ou tipo de local
    private double[] coordinates; // [longitude, latitude] para o Leaflet

    private Boolean isActive;
    private List<MapMeasurementDto> measurements;

    // --- Novos campos para suporte à cor dinâmica dos ícones no mapa ---
    private String airQualityDescription; // Ex: "Baixa (Boa)", "Moderada", etc.
    private String hexColor;              // Ex: "#2ecc71" (Verde), "#f1c40f" (Amarelo)
}