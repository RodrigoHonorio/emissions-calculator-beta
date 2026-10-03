package uk.org.spire.emissions_calculator_beta.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.org.spire.emissions_calculator_beta.entity.LondonBorough;
import uk.org.spire.emissions_calculator_beta.repository.LondonBoroughRepository;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class LondonBoroughSyncService {

    private final LondonBoroughRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Instanciado diretamente

    @Transactional
    public void sync() {
        if (repository.count() > 0) {
            log.info("ℹ️ Base de dados de Distritos já populada. Ignorando o seeding.");
            return;
        }

        log.info("🔄 A carregar london-boroughs.geojson para o PostGIS...");
        ClassPathResource resource = new ClassPathResource("static/data/london-boroughs.geojson");

        if (!resource.exists()) {
            log.warn("⚠️ Aviso: Ficheiro london-boroughs.geojson não encontrado.");
            return;
        }

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(inputStream);
            JsonNode features = root.get("features");
            GeoJsonReader reader = new GeoJsonReader();

            for (JsonNode feature : features) {
                String name = feature.get("properties").get("name").asText();
                String code = feature.get("properties").has("code")
                        ? feature.get("properties").get("code").asText()
                        : name.toLowerCase().replace(" ", "_");

                String geometryJson = feature.get("geometry").toString();
                Geometry parsedGeometry = reader.read(geometryJson);

                MultiPolygon multiPolygon;
                if (parsedGeometry instanceof MultiPolygon) {
                    multiPolygon = (MultiPolygon) parsedGeometry;
                } else if (parsedGeometry instanceof Polygon) {
                    multiPolygon = parsedGeometry.getFactory().createMultiPolygon(
                            new Polygon[]{(Polygon) parsedGeometry}
                    );
                } else {
                    continue;
                }

                multiPolygon.setSRID(4326);

                LondonBorough borough = LondonBorough.builder()
                        .name(name)
                        .code(code)
                        .boundary(multiPolygon)
                        .build();

                repository.save(borough);
            }
            log.info("✅ Todos os distritos foram importados com sucesso para o PostGIS!");
        } catch (Exception e) {
            log.error("❌ Erro ao processar london-boroughs.geojson", e);
        }
    }
}