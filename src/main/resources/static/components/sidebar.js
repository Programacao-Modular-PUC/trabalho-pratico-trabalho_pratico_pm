// components/sidebar.js
// Componente Sidebar — injetado em todas as páginas via initSidebar()

export function renderSidebar(paginaAtiva) {
    const itens = [
        { href: 'index.html',       icon: '🏠', label: 'Dashboard' },
        { href: 'residencias.html', icon: '🏡', label: 'Residências' },
        { href: 'clientes.html',    icon: '👥', label: 'Clientes' },
        { href: 'reservas.html',    icon: '📅', label: 'Reservas' },
        { href: 'alugueis.html',    icon: '🔑', label: 'Aluguéis' },
        { href: 'cadastro.html',    icon: '➕', label: 'Novo Cadastro' },
        { href: 'pagamentos.html',  icon: '💳', label: 'Pagamentos' },
    ];

    const linksHtml = itens.map(item => {
        const ativo = item.href === paginaAtiva ? 'class="active"' : '';
        return `<li><a href="${item.href}" ${ativo}><span class="icon">${item.icon}</span> ${item.label}</a></li>`;
    }).join('\n      ');

    return `
    <div class="sidebar">
      <div class="sidebar-brand">
        <div class="logo-mark">Hospedagem<br/>Maraú</div>
        <div class="logo-sub">Gestão de Hospedagem</div>
      </div>
      <ul class="sidebar-nav">
        ${linksHtml}
      </ul>
      <div class="sidebar-footer">PUC Minas · Prog. Modular</div>
    </div>`;
}

export function initSidebar(paginaAtiva) {
    const container = document.getElementById('sidebar-container');
    if (container) {
        container.innerHTML = renderSidebar(paginaAtiva);
    }
}
