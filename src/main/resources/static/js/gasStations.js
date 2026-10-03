window.GasStationsModule = (() => {
    async function load(layerGroup) {
        try {
            const response = await fetch('/api/v1/map/gas-stations');
            if (!response.ok) {
                return;
            }

            const data = await response.json();
            if (!Array.isArray(data)) return;

            data.forEach(station => {
                if (station.geometry && station.geometry.coordinates) {
                    const [lon, lat] = station.geometry.coordinates;

                    const marker = L.circleMarker([lat, lon], {
                        radius: 7,
                        fillColor: "#f59e0b",
                        color: "#ffffff",
                        weight: 1.5,
                        opacity: 1,
                        fillOpacity: 0.85
                    }).bindPopup(`