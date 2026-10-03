package uk.org.spire.emissions_calculator_beta.service.scientific;

public enum AirQualityLevel {
    LOW("Baixa (Boa)", "#2ecc71", 1, 3),        // Verde
    MODERATE("Moderada", "#f1c40f", 4, 6),     // Amarelo
    HIGH("Alta (Ruim)", "#e67e22", 7, 9),      // Laranja
    VERY_HIGH("Muito Alta (Perigosa)", "#e74c3c", 10, 10); // Vermelho

    private final String description;
    private final String hexColor;
    private final int minIndex;
    private final int maxIndex;

    AirQualityLevel(String description, String hexColor, int minIndex, int maxIndex) {
        this.description = description;
        this.hexColor = hexColor;
        this.minIndex = minIndex;
        this.maxIndex = maxIndex;
    }

    public static AirQualityLevel fromIndex(int index) {
        for (AirQualityLevel level : values()) {
            if (index >= level.minIndex && index <= level.maxIndex) {
                return level;
            }
        }
        return LOW; // Fallback de segurança
    }

    public String getDescription() {
        return description;
    }

    public String getHexColor() {
        return hexColor;
    }
}