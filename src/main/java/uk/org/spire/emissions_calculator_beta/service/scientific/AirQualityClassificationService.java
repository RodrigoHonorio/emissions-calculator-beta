package uk.org.spire.emissions_calculator_beta.service.scientific;

import org.springframework.stereotype.Service;

@Service
public class AirQualityClassificationService {

    /**
     * Classifica o nível de poluição com base no índice numérico oficial DAQI (1 a 10)
     * estabelecido pela Defra (Reino Unido).
     */
    public AirQualityLevel classifyByIndex(int daqiIndex) {
        return AirQualityLevel.fromIndex(daqiIndex);
    }

    /**
     * Classificação baseada na concentração de PM2.5 (µg/m³), alinhada
     * aos limiares orientativos da Defra para médias de 24 horas.
     */
    public AirQualityLevel classifyByPm25(double pm25Value) {
        if (pm25Value <= 35) {
            return AirQualityLevel.LOW;      // Índices 1-3
        } else if (pm25Value <= 53) {
            return AirQualityLevel.MODERATE; // Índices 4-6
        } else if (pm25Value <= 70) {
            return AirQualityLevel.HIGH;     // Índices 7-9
        } else {
            return AirQualityLevel.VERY_HIGH;// Índice 10
        }
    }
}