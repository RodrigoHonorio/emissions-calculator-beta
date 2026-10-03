document.addEventListener('DOMContentLoaded', () => {
    // 0. Inicializar o sistema de barras de progresso dinâmico
    LoaderModule.init();

    // 1. Inicializar o núcleo do mapa
    MapCore.init();

    // 2. Criar as camadas visuais associadas a cada entidade
    const boroughsLayer = MapCore.createLayerGroup('boroughs', 'London Boroughs', true);
    const gasLayer = MapCore.createLayerGroup('gasStations', 'Gas Stations', true);
    const airLayer = MapCore.createLayerGroup('airStations', 'Air Stations', true);

    // 3. Carregar os dados de cada módulo
    if (window.LondonBoroughsModule) {
        LondonBoroughsModule.load(boroughsLayer);
    }
    GasStationsModule.load(gasLayer);
    AirStationsModule.load(airLayer);

    // 4. Ligar os controlos da UI
    document.getElementById('toggleBoroughs')?.addEventListener('change', (e) => {
        MapCore.toggleLayer('boroughs', e.target.checked);
    });

    document.getElementById('toggleGas')?.addEventListener('change', (e) => {
        MapCore.toggleLayer('gasStations', e.target.checked);
    });

    document.getElementById('toggleAir')?.addEventListener('change', (e) => {
        MapCore.toggleLayer('airStations', e.target.checked);
    });

    // 5. Lógica de Controlo do Modal "About Author & Project"
    const authorModal = document.getElementById('authorModal');
    const openBtn = document.getElementById('openAuthorModal');
    const closeBtn = document.getElementById('closeAuthorModal');
    const closeBtnFooter = document.getElementById('closeAuthorModalBtn');

    function openModal() {
        if (!authorModal) return;
        authorModal.classList.remove('hidden');
        setTimeout(() => authorModal.classList.remove('opacity-0'), 10);
    }

    function closeModal() {
        if (!authorModal) return;
        authorModal.classList.add('opacity-0');
        setTimeout(() => authorModal.classList.add('hidden'), 300);
    }

    openBtn?.addEventListener('click', openModal);
    closeBtn?.addEventListener('click', closeModal);
    closeBtnFooter?.addEventListener('click', closeModal);

    // Fechar ao clicar fora do cartão
    authorModal?.addEventListener('click', (e) => {
        if (e.target === authorModal) closeModal();
    });
});