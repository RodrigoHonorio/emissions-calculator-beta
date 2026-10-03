package uk.org.spire.emissions_calculator_beta.constants;

/**
 * Define os níveis de severidade para os pontos críticos (Hotspots) de concentração de VOC.
 *
 * <h3>Contexto e Justificativa:</h3>
 * Estes limiares foram definidos para categorizar o impacto combinado da poluição de fundo
 * (Baseline) somada à pluma de evaporação dos postos de combustível na Grande Londres.
 *
 * <h3>Referência Oficial e Documentação:</h3>
 * <ul>
 *   <li><b>Organização:</b> Department for Environment, Food & Rural Affairs (DEFRA) / LAQN</li>
 *   <li><b>Contexto Legal/Técnico:</b> Air Quality Standards Regulations</li>
 *   <li><b>Link de Referência:</b> <a href="https://uk-air.defra.gov.uk/air-pollution/daqi">DEFRA Daily Air Quality Index (DAQI)</a></li>
 * </ul>
 */
public enum SeverityLevel {

    /**
     * Nível Moderado: A concentração está dentro dos parâmetros aceitáveis,
     * mas requer monitorização contínua em zonas de tráfego denso.
     */
    MODERATE,

    /**
     * Nível Alto: Ultrapassa os limiares de conforto recomendados para grupos sensíveis,
     * exigindo atenção nos relatórios diários de emissão evaporativa.
     */
    HIGH,

    /**
     * Nível Crítico: Violação dos limites seguros de emissão combinada de VOC,
     * disparando alertas imediatos no sistema de snapshots diários.
     */
    CRITICAL
}
