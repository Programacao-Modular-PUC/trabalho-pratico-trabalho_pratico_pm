// components/toast.js
// Componente de feedback visual para ações do usuário

export function showToast(mensagem, tipo = 'success') {
    const cores = {
        success: '#2ecc71',
        error:   '#e74c3c',
        warn:    '#f39c12',
        info:    '#3498db',
    };

    const toast = document.createElement('div');
    toast.textContent = mensagem;
    Object.assign(toast.style, {
        position:     'fixed',
        bottom:       '24px',
        right:        '24px',
        background:   cores[tipo] || cores.info,
        color:        '#fff',
        padding:      '12px 20px',
        borderRadius: '8px',
        fontSize:     '14px',
        fontFamily:   'Inter, sans-serif',
        boxShadow:    '0 4px 12px rgba(0,0,0,0.15)',
        zIndex:       '9999',
        transition:   'opacity 0.4s ease',
        opacity:      '1',
    });

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 400);
    }, 3500);
}
