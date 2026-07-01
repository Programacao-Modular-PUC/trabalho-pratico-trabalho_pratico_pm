// components/api.js
// Camada de acesso à API — centraliza fetch e tratamento de erros

const BASE_URL = 'http://localhost:8080';

async function request(method, path, body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' },
    };
    if (body) options.body = JSON.stringify(body);

    const response = await fetch(`${BASE_URL}${path}`, options);

    if (!response.ok) {
        const erro = await response.json().catch(() => ({ mensagem: response.statusText }));
        throw new Error(erro.mensagem || 'Erro desconhecido na API');
    }

    if (response.status === 204) return null;
    return response.json();
}

export const api = {
    get:    (path)         => request('GET',    path),
    post:   (path, body)   => request('POST',   path, body),
    put:    (path, body)   => request('PUT',    path, body),
    delete: (path)         => request('DELETE', path),

    // Aluguéis
    alugueis: {
        listar:            ()          => api.get('/alugueis'),
        buscar:            (id)        => api.get(`/alugueis/${id}`),
        criar:             (dados)     => api.post('/alugueis', dados),
        cancelar:          (id)        => api.delete(`/alugueis/${id}`),
        processarPagamento:(id, meio, dados) =>
            request('POST', `/alugueis/${id}/pagamento?meio=${meio}`, dados),
    },

    // Clientes
    clientes: {
        listar:  ()         => api.get('/clientes'),
        buscar:  (id)       => api.get(`/clientes/${id}`),
        criar:   (dados)    => api.post('/clientes', dados),
        atualizar:(id, d)   => api.put(`/clientes/${id}`, d),
        deletar: (id)       => api.delete(`/clientes/${id}`),
    },

    // Quartos
    quartos: {
        listar:             ()          => api.get('/quartos'),
        listarPorResidencia:(rid)       => api.get(`/quartos/residencia/${rid}`),
        listarPorTipo:      (tipo)      => api.get(`/quartos/tipo/${tipo}`),
        verificarDisp:      (id, e, s)  =>
            api.get(`/quartos/${id}/disponibilidade?entrada=${e}&saida=${s}`),
        criar:              (rid, dados) => api.post(`/quartos/residencia/${rid}`, dados),
        deletar:            (id)        => api.delete(`/quartos/${id}`),
    },

    // Residências
    residencias: {
        listar:   ()         => api.get('/residencias'),
        buscar:   (id)       => api.get(`/residencias/${id}`),
        criar:    (dados)    => api.post('/residencias', dados),
        atualizar:(id, d)    => api.put(`/residencias/${id}`, d),
        deletar:  (id)       => api.delete(`/residencias/${id}`),
    },
};
