import { api } from "./api.js";
import { showToast } from "./toast.js";

document.addEventListener("DOMContentLoaded", iniciar);

async function iniciar() {
    await carregarClientes();
    await carregarResidencias();
    await carregarReservas();

    document.getElementById("residencia-res").addEventListener("change", carregarQuartos);
    document.getElementById("btnConfirmarReserva").addEventListener("click", confirmarReserva);
    document.getElementById("btnAtualizarReservas").addEventListener("click", carregarReservas);
    document.getElementById("btnLimparReserva").addEventListener("click", limparFormulario);
}

async function carregarClientes() {
    const select = document.getElementById("cliente-res");
    const clientes = await api.clientes.listar();
    select.innerHTML = '<option value="">Selecione um cliente...</option>';
    clientes.forEach(cliente => {
        select.innerHTML += `<option value="${cliente.id}">${cliente.nome}</option>`;
    });
}

async function carregarResidencias() {
    const select = document.getElementById("residencia-res");
    const residencias = await api.residencias.listar();
    select.innerHTML = '<option value="">Selecione uma residencia...</option>';
    residencias.forEach(residencia => {
        select.innerHTML += `<option value="${residencia.id}">${residencia.endereco}, ${residencia.numero}</option>`;
    });
}

async function carregarQuartos() {
    const residenciaId = document.getElementById("residencia-res").value;
    const select = document.getElementById("quarto-res");
    select.innerHTML = '<option value="">Selecione um quarto...</option>';

    if (!residenciaId) return;

    const quartos = await api.quartos.listarPorResidencia(residenciaId);
    quartos.forEach(quarto => {
        const adicionais = [
            quarto.possuiAR ? "ar" : null,
            quarto.possuiHidro ? "hidro" : null
        ].filter(Boolean).join(", ");
        select.innerHTML += `<option value="${quarto.id}">Quarto ${quarto.id} - R$ ${quarto.valorBase.toFixed(2)} ${adicionais}</option>`;
    });
}

async function confirmarReserva() {
    const dataEntrada = document.getElementById("entrada-res").value;
    const dataSaida = document.getElementById("saida-res").value;

    if (!dataEntrada || !dataSaida) {
        showToast("Informe entrada e saida da reserva.", "error");
        return;
    }

    if (new Date(dataEntrada) <= new Date()) {
        showToast("Reserva futura precisa ter entrada posterior ao momento atual.", "error");
        return;
    }

    const reserva = {
        clienteId: Number(document.getElementById("cliente-res").value),
        residenciaId: Number(document.getElementById("residencia-res").value),
        quartoId: Number(document.getElementById("quarto-res").value),
        dataEntrada,
        dataSaida,
        solicitouBerco: false,
        numHospedes: Number(document.getElementById("hospedes-res").value || 1)
    };

    try {
        await api.alugueis.criar(reserva);
        showToast("Reserva criada com sucesso!");
        limparFormulario();
        await carregarReservas();
    } catch (erro) {
        showToast(erro.message, "error");
    }
}

async function carregarReservas() {
    const tbody = document.getElementById("reservasTabela");
    const alugueis = await api.alugueis.listar();
    const reservas = alugueis.filter(aluguel => aluguel.reservaFutura && !aluguel.cancelado);

    if (reservas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9">Nenhuma reserva futura cadastrada.</td></tr>';
        return;
    }

    tbody.innerHTML = "";
    reservas.forEach(reserva => {
        tbody.innerHTML += `
            <tr>
                <td>${reserva.cliente.nome}</td>
                <td>${reserva.residencia.endereco}</td>
                <td>Quarto ${reserva.quarto.id}</td>
                <td>${formatarData(reserva.dataEntrada)}</td>
                <td>${formatarData(reserva.dataSaida)}</td>
                <td>${reserva.qtdDiarias}</td>
                <td>R$ ${reserva.valorFinal.toFixed(2)}</td>
                <td><span class="badge badge-gray">Reservado</span></td>
                <td><button class="btn btn-ghost btn-sm" data-cancelar="${reserva.id}">Cancelar</button></td>
            </tr>
        `;
    });

    tbody.querySelectorAll("[data-cancelar]").forEach(botao => {
        botao.addEventListener("click", async () => cancelarReserva(botao.dataset.cancelar));
    });
}

async function cancelarReserva(id) {
    try {
        await api.alugueis.cancelar(id);
        showToast("Reserva cancelada.");
        await carregarReservas();
    } catch (erro) {
        showToast(erro.message, "error");
    }
}

function limparFormulario() {
    document.getElementById("cliente-res").value = "";
    document.getElementById("residencia-res").value = "";
    document.getElementById("quarto-res").innerHTML = '<option value="">Selecione uma residencia...</option>';
    document.getElementById("entrada-res").value = "";
    document.getElementById("saida-res").value = "";
    document.getElementById("hospedes-res").value = "1";
}

function formatarData(data) {
    return new Date(data).toLocaleString("pt-BR");
}
