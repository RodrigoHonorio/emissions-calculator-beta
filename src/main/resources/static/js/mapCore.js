window.MapCore = (() => {
    let map;
    const layers = {};

    function init() {
        map = L.map('map', { zoomControl: false }).setView([51.5074, -0.1278], 12);
        L.control.zoom({ position: 'topright' }).addTo(map);

        // Mapa minimalista, suave e limpo (Esri Light Gray Canvas)
        L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/Canvas/World_Light_Gray_Base/MapServer/tile/{z}/{y}/{x}', {
            maxZoom: 19,
            attribution: 'Tiles &copy; Esri &mdash; Esri, DeLorme, NAVTEQ'
        }).addTo(map);
    }

    function createLayerGroup(key, label, defaultVisible = true) {
        const group = L.layerGroup();
        if (defaultVisible) {
            group.addTo(map);
        }
        layers[key] = group;
        return group;
    }

    function toggleLayer(key, visible) {
        if (!layers[key]) return;
        if (visible) {
            map.addLayer(layers[key]);
        } else {
            map.removeLayer(layers[key]);
        }
    }

    return { init, createLayerGroup, toggleLayer };
})();