// components/topbar.js
// Componente Topbar — injetado em todas as páginas via initTopbar()

export function renderTopbar(titulo) {
    return `
    <div class="topbar">
      <span class="topbar-title">${titulo}</span>
      <div class="topbar-user">
        <span>Bem-vindo, Admin</span>
        <div class="avatar">A</div>
      </div>
    </div>`;
}

export function initTopbar(titulo) {
    const container = document.getElementById('topbar-container');
    if (container) {
        container.innerHTML = renderTopbar(titulo);
    }
}
