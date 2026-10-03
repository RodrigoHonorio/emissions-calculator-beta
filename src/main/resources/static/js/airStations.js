window.AirStationsModule = (() => {

    function hexToRgba(hex, alpha = 0.15) {
        if (!hex || typeof hex !== 'string') return `rgba(16, 185, 129, ${alpha})`;
        let cleanHex = hex.replace('#', '');
        if (cleanHex.length === 3) {
            cleanHex = cleanHex.split('').map(c => c + c).join('');
        }
        const num = parseInt(cleanHex, 16);
        if (isNaN(num)) return `rgba(16, 185, 129, ${alpha})`;
        const r = (num >> 16) & 255;
        const g = (num >> 8) & 255;
        const b = num & 255;
        return `rgba(${r}, ${g}, ${b}, ${alpha})`;
    }

    /**
     * Calcula a cor individual de cada medição com base nos critérios Defra / DAQI
     */
    function getMeasurementColor(pollutant, val) {
        if (val === undefined || val === null) return '#2ecc71';
        const num = parseFloat(val);
        if (isNaN(num)) return '#2ecc71';

        const name = (pollutant || '').toUpperCase();

        // 1. Índice DAQI / AQI
        if (name.includes('AQI') || name.includes('DAQI')) {
            if (num <= 3) return '#2ecc71';      // Baixa (Verde)
            if (num <= 6) return '#f1c40f';      // Moderada (Amarelo)
            if (num <= 9) return '#e67e22';      // Alta (Laranja)
            return '#e74c3c';                    // Muito Alta (Vermelho)
        }

        // 2. PM2.5 (µg/m³)
        if (name.includes('PM2.5') || name.includes('PM25')) {
            if (num <= 35) return '#2ecc71';
            if (num <= 53) return '#f1c40f';
            if (num <= 70) return '#e67e22';
            return '#e74c3c';
        }

        // 3. PM10 (µg/m³)
        if (name.includes('PM10')) {
            if (num <= 50) return '#2ecc71';
            if (num <= 75) return '#f1c40f';
            if (num <= 100) return '#e67e22';
            return '#e74c3c';
        }

        // 4. Dióxido de Azoto (NO2 µg/m³)
        if (name.includes('NO2')) {
            if (num <= 200) return '#2ecc71';
            if (num <= 267) return '#f1c40f';
            if (num <= 400) return '#e67e22';
            return '#e74c3c';
        }

        // 5. Ozono (O3 µg/m³)
        if (name.includes('O3')) {
            if (num <= 100) return '#2ecc71';
            if (num <= 160) return '#f1c40f';
            if (num <= 240) return '#e67e22';
            return '#e74c3c';
        }

        return '#2ecc71'; // Fallback padrão (Verde)
    }

    async function load(layerGroup) {
        try {
            const response = await fetch('/api/v1/map/air-stations');
            if (!response.ok) throw new Error('Erro ao obter as estações de qualidade do ar da API');

            const stations = await response.json();
            if (!Array.isArray(stations)) return;

            stations.forEach(station => {
                if (station.coordinates && station.coordinates.length === 2) {
                    const lng = station.coordinates[0];
                    const lat = station.coordinates[1];

                    // Cores e classificação geral vindas da API
                    const mainColor = station.hexColor || "#2ecc71";
                    const qualityText = station.airQualityDescription || 'Boa';
                    const isActive = station.isActive !== undefined ? station.isActive : true;

                    // Badge do estado da estação (Ativa / Inativa)
                    const statusBadgeHtml = isActive
                        ? `<span style="background-color: #dcfce7; color: #16a34a; font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 12px; white-space: nowrap;">Ativa</span>`
                        : `<span style="background-color: #fee2e2; color: #dc2626; font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 12px; white-space: nowrap;">Inativa</span>`;

                    // Selo do nível global de qualidade do ar
                    const qualityBadgeHtml = `
                        <div style="margin-top: 6px; display: inline-flex; align-items: center; gap: 6px; background-color: ${hexToRgba(mainColor, 0.12)}; border: 1px solid ${hexToRgba(mainColor, 0.35)}; padding: 3px 8px; border-radius: 6px;">
                            <span style="width: 8px; height: 8px; border-radius: 50%; background-color: ${mainColor}; flex-shrink: 0;"></span>
                            <span style="font-size: 11px; font-weight: 600; color: #1e293b;">Qualidade: <strong style="color: ${mainColor};">${qualityText}</strong></span>
                        </div>
                    `;

                    // Marcador circular no mapa
                    const marker = L.circleMarker([lat, lng], {
                        radius: 7,
                        fillColor: mainColor,
                        color: "#ffffff",
                        weight: 1.5,
                        opacity: 1,
                        fillOpacity: 0.9
                    });

                    // Formatação dos cartões individuais de cada medição
                    let measurementsGridHtml = '';
                    if (station.measurements && Array.isArray(station.measurements) && station.measurements.length > 0) {
                        const items = station.measurements.map(m => {
                            const label = m.pollutant || m.name || m.code || m.type || 'Medição';
                            const val = m.value !== undefined ? m.value : '0';
                            const unit = m.unit ? ` ${m.unit}` : '';

                            // Cor individual do poluente
                            const itemColor = getMeasurementColor(label, m.value);
                            const bgRgba = hexToRgba(itemColor, 0.15);
                            const borderRgba = hexToRgba(itemColor, 0.35);

                            return `
                                <div style="background-color: ${bgRgba}; border: 1px solid ${borderRgba}; padding: 4px 7px; border-radius: 6px; font-size: 11px; display: flex; align-items: center; justify-content: space-between; gap: 4px;">
                                    <strong style="color: #0f172a; font-weight: 700;">${label}:</strong>
                                    <span style="color: #1e293b; font-weight: 600; white-space: nowrap;">${val}${unit}</span>
                                </div>
                            `;
                        }).join('');

                        measurementsGridHtml = `
                            <div style="margin-top: 10px;">
                                <div style="display: flex; align-items: center; font-size: 11px; font-weight: 700; color: #1e293b; margin-bottom: 6px;">
                                    <svg style="width: 13px; height: 13px; margin-right: 5px; color: #64748b;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
                                    </svg>
                                    Últimas Medições:
                                </div>
                                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 6px;">
                                    ${items}
                                </div>
                            </div>
                        `;
                    }

                    // Popup final
                    const popupContent = `
                        <div style="font-family: system-ui, -apple-system, sans-serif; color: #0f172a; min-width: 230px; padding: 2px;">
                            <div style="display: flex; align-items: center; justify-content: space-between; gap: 8px; margin-bottom: 4px;">
                                <div style="display: flex; align-items: center; gap: 6px;">
                                    <svg style="width: 16px; height: 16px; color: #0284c7; flex-shrink: 0;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5m0 0v-5a2 2 0 012-2h2a2 2 0 012 2v5m-4 0h4"></path>
                                    </svg>
                                    <strong style="font-size: 13px; font-weight: 700; color: #0f172a; line-height: 1.2;">${station.name || 'Estação'}</strong>
                                </div>
                                ${statusBadgeHtml}
                            </div>

                            <div style="font-size: 11px; color: #64748b; line-height: 1.5; margin-left: 22px;">
                                <div><strong style="color: #475569;">Código:</strong> ${station.subtitle || 'N/A'}</div>
                                <div><strong style="color: #475569;">Tipo:</strong> ${station.details || 'N/A'}</div>
                                ${qualityBadgeHtml}
                            </div>

                            ${measurementsGridHtml}
                        </div>
                    `;

                    marker.bindPopup(popupContent, { maxWidth: 290 });
                    marker.addTo(layerGroup);
                }
            });

            console.log('✅ Estações de Ar carregadas com sucesso!');

        } catch (error) {
            console.error('Erro ao carregar o mapa das estações de ar:', error);
        }
    }

    return { load };
})();