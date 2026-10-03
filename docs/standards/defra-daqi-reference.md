# Referência Técnica: UK Daily Air Quality Index (DAQI)

Este documento serve como referência oficial para o motor de cálculo científico integrado no projeto **Spire Emissions Calculator Beta**.

## 1. Visão Geral
O **DAQI** é o índice oficial de qualidade do ar utilizado no Reino Unido, desenvolvido pelo *Department for Environment, Food & Rural Affairs* (Defra) em conjunto com parceiros científicos. Ele padroniza os níveis de poluição do ar em faixas compreensíveis.

## 2. Bandas e Classificação de Cores para o Mapa

| Banda DAQI | Índice Numérico | Categoria | Cor Sugerida (HEX) | Descrição do Impacto |
| :--- | :--- | :--- | :--- | :--- |
| **Low** | 1 - 3 | Baixa (Boa) | `#2ecc71` (Verde) | Qualidade do ar ótima/aceitável. |
| **Moderate** | 4 - 6 | Moderada | `#f1c40f` (Amarelo) | Efeitos leves em pessoas altamente sensíveis. |
| **High** | 7 - 9 | Alta (Ruim) | `#e67e22` (Laranja) | Possível impacto em grupos sensíveis. |
| **Very High** | 10 | Muito Alta (Perigosa) | `#e74c3c` (Vermelho) | Efeitos graves na saúde da população exposta. |

## 3. Fontes Oficiais
* [UK-AIR Defra DAQI Guide](https://uk-air.defra.gov.uk/air-pollution/daqi)