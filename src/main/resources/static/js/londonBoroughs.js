window.LondonBoroughsModule = (() => {
    async function load(layerGroup) {
        try {
            // Busca os dados diretamente da API Java
            const response = await fetch('/api/boroughs');
            if (!response.ok) throw new Error('Erro ao obter os distritos da API PostGIS');

            const boroughs = await response.json();

            // Mapeia e faz o parse da propriedade 'boundary' vinda de cada registro do banco
            const features = boroughs
                .filter(b => b.boundary)
                .map(b => {
                    const geometry = typeof b.boundary === 'string' ? JSON.parse(b.boundary) : b.boundary;
                    return {
                        type: "Feature",
                        properties: {
                            name: b.name,
                            code: b.code
                        },
                        geometry: geometry
                    };
                });

            const geojsonData = {
                type: "FeatureCollection",
                features: features
            };

            // 1. Desenha os distritos (camada interativa)
            const boroughsLayer = L.geoJSON(geojsonData, {
                style: {
                    color: "#0284c7",
                    weight: 1,
                    fillColor: "#38bdf8",
                    fillOpacity: 0.12
                },
                onEachFeature: (feature, layer) => {
                    if (feature.properties && feature.properties.name) {
                        layer.bindPopup(`
                            <div style="font-family: sans-serif; color: #0f172a;">
                                <strong style="font-size: 14px;">${feature.properties.name}</strong><br>
                                <span style="font-size: 12px; color: #64748b;">Código: ${feature.properties.code || 'N/A'}</span>
                            </div>
                        `);
                    }

                    layer.on({
                        mouseover: (e) => {
                            e.target.setStyle({ fillOpacity: 0.35, weight: 2, color: '#0369a1' });
                        },
                        mouseout: (e) => {
                            boroughsLayer.resetStyle(e.target);
                        }
                    });
                }
            });

            // 2. Desenha o contorno externo destacado da Grande Londres
            const outerBoundaryLayer = L.geoJSON(geojsonData, {
                style: {
                    color: "#0f172a",
                    weight: 2.5,
                    fill: false,
                    interactive: false
                }
            });

            layerGroup.addLayer(boroughsLayer);
            layerGroup.addLayer(outerBoundaryLayer);

            console.log('✅ Polígonos de Londres carregados com sucesso a partir do PostGIS!');

        } catch (error) {
            console.error('Erro ao carregar o mapa via API PostGIS:', error);
        }
    }

    return { load };
})();