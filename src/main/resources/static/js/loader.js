window.LoaderModule = (() => {
    let container;

    function init() {
        container = document.getElementById('progress-container');
        startBackfillPolling();
    }

    /**
     * Mostra ou atualiza uma barra de progresso específica
     * @param {string} id - Identificador único (ex: 'backfill', 'clima', 'postos')
     * @param {string} text - Texto descritivo do que está a ser carregado
     * @param {number} percentage - Valor percentual (0 a 100)
     */
    function showProgress(id, text, percentage) {
        if (!container) return;
        let item = document.getElementById(`progress-${id}`);
        if (!item) {
            item = document.createElement('div');
            item.id = `progress-${id}`;
            item.className = 'backfill-box';
            item.innerHTML = `
                <div class="backfill-header">
                    <span id="text-${id}">${text}</span>
                    <span id="pct-${id}">${percentage}%</span>
                </div>
                <div class="backfill-track">
                    <div id="bar-${id}" class="backfill-fill" style="width: ${percentage}%"></div>
                </div>
            `;
            container.appendChild(item);
        } else {
            document.getElementById(`text-${id}`).innerText = text;
            document.getElementById(`pct-${id}`).innerText = `${percentage}%`;
            document.getElementById(`bar-${id}`).style.width = `${percentage}%`;
        }
    }

    /**
     * Remove a barra de progresso quando o processo termina
     */
    function hideProgress(id) {
        const item = document.getElementById(`progress-${id}`);
        if (item) {
            item.remove();
        }
    }

    // Monitorização automática do Backfill vindo do backend
    function startBackfillPolling() {
        function poll() {
            fetch('/api/backfill/progress')
                .then(res => res.json())
                .then(data => {
                    if (data.running) {
                        showProgress('backfill', data.currentMessage, data.percentage);
                        setTimeout(poll, 1000);
                    } else {
                        hideProgress('backfill');
                        setTimeout(poll, 3000); // Verifica periodicamente se voltou a correr
                    }
                })
                .catch(() => {
                    setTimeout(poll, 5000);
                });
        }
        poll();
    }

    return { init, showProgress, hideProgress };
})();