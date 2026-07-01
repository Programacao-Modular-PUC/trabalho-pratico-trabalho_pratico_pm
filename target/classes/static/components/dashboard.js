import { api } from "./api.js";

document.addEventListener("DOMContentLoaded", carregarDashboard);

async function carregarDashboard() {
    try {
        const [residencias, quartos, clientes, alugueis] = await Promise.all([
            api.residencias.listar(),
            api.quartos.listar(),
            api.clientes.listar(),
            api.alugueis.listar()
        ]);

        const agora = new Date();
        const reservas = alugueis.filter(aluguel => aluguel.reservaFutura && !aluguel.cancelado);
        const quartosOcupados = new Set(
            alugueis
                .filter(aluguel => estaAtivo(aluguel, agora))
                .map(aluguel => aluguel.quarto?.id)
                .filter(Boolean)
        );

        document.getElementById("totalResidencias").textContent = residencias.length;
        document.getElementById("totalQuartos").textContent = Math.max(0, quartos.length - quartosOcupados.size);
        document.getElementById("totalReservas").textContent = reservas.length;
        document.getElementById("totalClientes").textContent = clientes.length;

        renderizarAlugueis(alugueis);
        renderizarReservas(reservas);
    } catch (erro) {
        console.error(erro);
        document.getElementById("dashboardAlugueis").innerHTML =
            '<tr><td colspan="8">Nao foi possivel carregar os dados do dashboard.</td></tr>';
        document.getElementById("dashboardReservas").innerHTML =
            '<tr><td colspan="5">Nao foi possivel carregar as reservas.</td></tr>';
    }
}

function renderizarAlugueis(alugueis) {
    const tbody = document.getElementById("dashboardAlugueis");
    const recentes = [...alugueis]
        .sort((a, b) => new Date(b.dataEntrada) - new Date(a.dataEntrada))
        .slice(0, 5);

    if (recentes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8">Nenhum aluguel registrado ainda.</td></tr>';
        return;
    }

    tbody.innerHTML = recentes.map(aluguel => `
        <tr>
            <td>${valorSeguro(aluguel.cliente?.nome)}</td>
            <td>${formatarResidencia(aluguel.residencia)}</td>
            <td>Quarto ${valorSeguro(aluguel.quarto?.id)}</td>
            <td>${formatarData(aluguel.dataEntrada)}</td>
            <td>${formatarData(aluguel.dataSaida)}</td>
            <td>${valorSeguro(aluguel.qtdDiarias)}</td>
            <td>${moeda(aluguel.valorFinal)}</td>
            <td>${badgeStatus(aluguel)}</td>
        </tr>
    `).join("");
}

function renderizarReservas(reservas) {
    const tbody = document.getElementById("dashboardReservas");
    const proximas = [...reservas]
        .sort((a, b) => new Date(a.dataEntrada) - new Date(b.dataEntrada))
        .slice(0, 5);

    if (proximas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">Nenhuma reserva futura cadastrada.</td></tr>';
        return;
    }

    tbody.innerHTML = proximas.map(reserva => `
        <tr>
            <td>${valorSeguro(reserva.cliente?.nome)}</td>
            <td>${formatarResidencia(reserva.residencia)} - Quarto ${valorSeguro(reserva.quarto?.id)}</td>
            <td>${formatarData(reserva.dataEntrada)}</td>
            <td>${formatarData(reserva.dataSaida)}</td>
            <td><a href="alugel.html" class="btn btn-primary btn-sm">Ver aluguel</a></td>
        </tr>
    `).join("");
}

function badgeStatus(aluguel) {
    if (aluguel.cancelado) {
        return '<span class="badge badge-red">Cancelado</span>';
    }
    if (aluguel.reservaFutura) {
        return '<span class="badge badge-gray">Reservado</span>';
    }
    if (new Date(aluguel.dataSaida) < new Date()) {
        return '<span class="badge badge-blue">Concluido</span>';
    }
    return '<span class="badge badge-green">Ativo</span>';
}

function estaAtivo(aluguel, agora) {
    if (aluguel.cancelado || aluguel.reservaFutura) return false;
    return new Date(aluguel.dataEntrada) <= agora && new Date(aluguel.dataSaida) >= agora;
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