import { api } from "./api.js";
import { showToast } from "./toast.js";

let residenciasCache = [];

document.addEventListener("DOMContentLoaded", iniciar);

async function iniciar() {
    const lista = document.getElementById("listaResidencias");
    if (!lista) return;

    lista.addEventListener("click", tratarCliqueResidencia);
    await carregarResidencias();
}

async function carregarResidencias() {
    try {
        residenciasCache = await api.residencias.listar();
        renderizarResidencias(residenciasCache);
    } catch (erro) {
        console.error(erro);
        showToast(erro.message, "error");
    }
}

function renderizarResidencias(residencias) {
    const lista = document.getElementById("listaResidencias");

    if (residencias.length === 0) {
        lista.innerHTML = `
            <div class="card empty-state">
                <div class="empty-icon">🏡</div>
                <h3>Nenhuma residencia encontrada</h3>
                <p>Cadastre uma nova residencia para comecar.</p>
            </div>
        `;
        return;
    }

    lista.innerHTML = residencias.map(residencia => `
        <div class="card" data-residencia-card="${residencia.id}">
            <div class="card-header">
                <div>
                    <h2 class="card-title">🏡 ${valorSeguro(residencia.endereco)}</h2>
                    <p style="font-size:13px;color:var(--texto-leve);margin-top:4px;">
                        Nº ${valorSeguro(residencia.numero)}
                        · Bairro ${valorSeguro(residencia.bairro)}
                        · CEP ${valorSeguro(residencia.cep)}
                        · ${valorSeguro(residencia.telefone)}
                        · ${valorSeguro(residencia.email)}
                    </p>
                </div>

                <div style="display:flex;gap:8px;flex-wrap:wrap;">
                    <button class="btn btn-ghost btn-sm" type="button" data-action="editar" data-id="${residencia.id}">
                        ✏️ Editar
                    </button>

                    <button class="btn btn-ghost btn-sm" type="button" data-action="historico" data-id="${residencia.id}">
                        📋 Histórico
                    </button>

                    <button class="btn btn-ghost btn-sm" type="button" data-action="excluir" data-id="${residencia.id}">
                        🗑️ Excluir
                    </button>
                </div>
            </div>

            <p class="section-title">Quartos</p>

            <div class="quartos-grid">
                ${renderizarQuartos(residencia.quartos)}
            </div>
        </div>
    `).join("");
}

function renderizarQuartos(quartos = []) {
    if (!quartos || quartos.length === 0) {
        return `
            <div style="padding:20px;color:gray;">
                Nenhum quarto cadastrado.
            </div>
        `;
    }

    return quartos.map((quarto, index) => `
        <div class="quarto-card">
            <div class="quarto-card-header">
                <div>
                    <div class="quarto-numero">Quarto ${index + 1}</div>
                    <div class="quarto-tipo">${valorSeguro(quarto.tipo_quarto ?? quarto.tipoQuarto)}</div>
                    <div style="font-size:14px;font-weight:600;color:white;margin-top:4px;">
                        ${moeda(quarto.valorBase)}
                    </div>
                </div>
                <span class="badge badge-green">Disponível</span>
            </div>

            <div class="quarto-body">
                <div class="quarto-amenities">
                    ${quarto.possuiAR ? '<span class="amenity-tag">❄️ Ar-condicionado</span>' : ""}
                    ${quarto.possuiHidro ? '<span class="amenity-tag">🛁 Hidromassagem</span>' : ""}
                </div>
            </div>
        </div>
    `).join("");
}

async function tratarCliqueResidencia(event) {
    const botao = event.target.closest("[data-action]");
    if (!botao) return;

    const id = Number(botao.dataset.id);
    const acao = botao.dataset.action;

    if (acao === "editar") {
        await editarResidencia(id);
    }

    if (acao === "historico") {
        await historicoResidencia(id);
    }

    if (acao === "excluir") {
        await excluirResidencia(id);
    }
}

async function editarResidencia(id) {
    const residencia = residenciasCache.find(item => Number(item.id) === id);
    if (!residencia) return;

    const endereco = prompt("Endereço:", residencia.endereco ?? "");
    if (endereco === null) return;

    const numero = prompt("Número:", residencia.numero ?? "");
    if (numero === null) return;

    const bairro = prompt("Bairro:", residencia.bairro ?? "");
    if (bairro === null) return;

    const cep = prompt("CEP:", residencia.cep ?? "");
    if (cep === null) return;

    const telefone = prompt("Telefone:", residencia.telefone ?? "");
    if (telefone === null) return;

    const email = prompt("Email:", residencia.email ?? "");
    if (email === null) return;

    try {
        await api.residencias.atualizar(id, {
            endereco,
            numero,
            bairro,
            cep,
            telefone,
            email
        });

        showToast("Residencia atualizada com sucesso!");
        await carregarResidencias();
    } catch (erro) {
        console.error(erro);
        showToast(erro.message, "error");
    }
}

async function historicoResidencia(id) {
    const residencia = residenciasCache.find(item => Number(item.id) === id);

    try {
        const historico = await api.get(`/residencias/${id}/historico`);

        if (historico.length === 0) {
            alert(`A residencia ${formatarResidencia(residencia)} ainda nao possui alugueis.`);
            return;
        }

        const linhas = historico.map(aluguel =>
            `${formatarData(aluguel.dataEntrada)} ate ${formatarData(aluguel.dataSaida)} | ${valorSeguro(aluguel.cliente?.nome)} | ${moeda(aluguel.valorFinal)}`
        ).join("\n");

        alert(`Historico de ${formatarResidencia(residencia)}:\n\n${linhas}`);
    } catch (erro) {
        console.error(erro);
        showToast(erro.message, "error");
    }
}

async function excluirResidencia(id) {
    const residencia = residenciasCache.find(item => Number(item.id) === id);
    const nome = formatarResidencia(residencia);

    if (!confirm(`Excluir a residencia ${nome}?`)) {
        return;
    }

    try {
        await api.residencias.deletar(id);
        showToast("Residencia excluida com sucesso!");
        await carregarResidencias();
    } catch (erro) {
        console.error(erro);
        showToast(
            "Nao foi possivel excluir. Verifique se ha quartos ou alugueis vinculados a esta residencia.",
            "error"
        );
    }
}

function formatarResidencia(residencia) {
    if (!residencia) return "-";
    return [residencia.endereco, residencia.numero].filter(Boolean).join(", ") || "-";
}

function formatarData(data) {
    if (!data) return "-";
    return new Date(data).toLocaleString("pt-BR");
}

function moeda(valor) {
    return Number(valor || 0).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });
}

function valorSeguro(valor) {
    return valor === null || valor === undefined || valor === "" ? "-" : String(valor);
}
