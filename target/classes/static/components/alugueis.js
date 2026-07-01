import { api } from "./api.js";
import { showToast } from "./toast.js";

let ultimoRecibo = null;
let quartosDaResidencia = [];

document.addEventListener("DOMContentLoaded", iniciar);

async function iniciar() {
    await carregarClientes();
    await carregarResidencias();
    await carregarHistorico();

    document.getElementById("residencia-alg").addEventListener("change", carregarQuartos);
    document.getElementById("quarto-alg").addEventListener("change", atualizarAdicionaisDoQuarto);
    document.getElementById("btnRegistrarAluguel").addEventListener("click", registrarAluguel);
    document.getElementById("btnLimparAluguel").addEventListener("click", limparFormulario);
    document.getElementById("btnImprimir").addEventListener("click", imprimirRecibo);
    document.getElementById("btnPdf").addEventListener("click", exportarPDF);

    document.getElementById("historicoAlugueis").addEventListener("click", async (event) => {
        const botao = event.target.closest("[data-recibo-id]");
        if (botao) {
            await verRecibo(botao.dataset.reciboId);
        }
    });
}

async function carregarClientes() {
    const select = document.getElementById("cliente-alg");
    const clientes = await api.clientes.listar();

    select.innerHTML = '<option value="">Selecione um cliente...</option>';

    clientes.forEach(cliente => {
        select.innerHTML += `
            <option value="${cliente.id}">
                ${cliente.nome}
            </option>
        `;
    });
}

async function carregarResidencias() {
    const select = document.getElementById("residencia-alg");
    const residencias = await api.residencias.listar();

    select.innerHTML = '<option value="">Selecione uma residencia...</option>';

    residencias.forEach(residencia => {
        select.innerHTML += `
            <option value="${residencia.id}">
                ${formatarResidencia(residencia)}
            </option>
        `;
    });
}

async function carregarQuartos() {
    const residenciaId = document.getElementById("residencia-alg").value;
    const select = document.getElementById("quarto-alg");

    select.innerHTML = '<option value="">Selecione um quarto...</option>';

    if (!residenciaId) return;

    const quartos = await api.quartos.listarPorResidencia(residenciaId);
    quartosDaResidencia = quartos;

    quartos.forEach(quarto => {
        select.innerHTML += `
            <option value="${quarto.id}">
                Quarto ${quarto.id} - ${moeda(quarto.valorBase)}
            </option>
        `;
    });

    atualizarAdicionaisDoQuarto();
}

async function registrarAluguel() {
    const clienteId = Number(document.getElementById("cliente-alg").value);
    const residenciaId = Number(document.getElementById("residencia-alg").value);
    const quartoId = Number(document.getElementById("quarto-alg").value);
    const dataEntrada = document.getElementById("entrada-alg").value;
    const dataSaida = document.getElementById("saida-alg").value;

    if (!clienteId || !residenciaId || !quartoId || !dataEntrada || !dataSaida) {
        showToast("Preencha cliente, residencia, quarto, entrada e saida.", "error");
        return;
    }

    const aluguel = {
        clienteId,
        residenciaId,
        quartoId,
        dataEntrada,
        dataSaida,
        solicitouBerco: false,
        numHospedes: 1
    };

    try {
        const resposta = await api.alugueis.criar(aluguel);

        preencherRecibo(resposta);
        await carregarHistorico();

        showToast("Aluguel registrado com sucesso!");
    } catch (erro) {
        console.error(erro);
        showToast(erro.message, "error");
    }
}

function preencherRecibo(aluguel) {
    ultimoRecibo = aluguel;

    document.getElementById("recCliente").textContent = valorSeguro(aluguel.cliente?.nome);
    document.getElementById("recCpf").textContent = valorSeguro(aluguel.cliente?.cpf);
    document.getElementById("recResidencia").textContent = formatarResidencia(aluguel.residencia);
    document.getElementById("recQuarto").textContent = valorSeguro(aluguel.quarto?.id);
    document.getElementById("recEntrada").textContent = formatarData(aluguel.dataEntrada);
    document.getElementById("recSaida").textContent = formatarData(aluguel.dataSaida);
    document.getElementById("recDiarias").textContent = valorSeguro(aluguel.qtdDiarias);
    document.getElementById("recValor").textContent = moeda(aluguel.valorFinal);
    document.getElementById("recTotal").textContent = moeda(aluguel.valorFinal);
    document.getElementById("recAr").textContent = aluguel.quarto?.possuiAR ? "Incluido" : "Nao incluido";
    document.getElementById("recHidro").textContent = aluguel.quarto?.possuiHidro ? "Incluido" : "Nao incluido";
}

async function carregarHistorico() {
    const tbody = document.getElementById("historicoAlugueis");
    const alugueis = await api.alugueis.listar();

    if (alugueis.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7">Nenhum aluguel registrado.</td></tr>';
        return;
    }

    tbody.innerHTML = "";

    alugueis.forEach(aluguel => {
        tbody.innerHTML += `
            <tr>
                <td>${valorSeguro(aluguel.cliente?.nome)}</td>
                <td>${formatarResidencia(aluguel.residencia)} - Quarto ${valorSeguro(aluguel.quarto?.id)}</td>
                <td>${formatarData(aluguel.dataEntrada)}</td>
                <td>${formatarData(aluguel.dataSaida)}</td>
                <td>${valorSeguro(aluguel.qtdDiarias)}</td>
                <td>${moeda(aluguel.valorFinal)}</td>
                <td>
                    <button class="btn btn-outline btn-sm" type="button" data-recibo-id="${aluguel.id}">
                        Ver
                    </button>
                </td>
            </tr>
        `;
    });
}

async function verRecibo(id) {
    try {
        const aluguel = await api.alugueis.buscar(id);

        preencherRecibo(aluguel);

        document.querySelector(".recibo")?.scrollIntoView({
            behavior: "smooth",
            block: "center"
        });

        showToast("Recibo carregado.");
    } catch (erro) {
        console.error(erro);
        showToast(erro.message, "error");
    }
}

function imprimirRecibo() {
    abrirReciboParaImpressao("print");
}

function exportarPDF() {
    abrirReciboParaImpressao("pdf");
}

function limparFormulario() {
    document.getElementById("cliente-alg").value = "";
    document.getElementById("residencia-alg").value = "";
    document.getElementById("quarto-alg").innerHTML = '<option value="">Selecione um quarto...</option>';
    quartosDaResidencia = [];
    document.getElementById("entrada-alg").value = "";
    document.getElementById("saida-alg").value = "";
    document.getElementById("algAr").checked = false;
    document.getElementById("algHidro").checked = false;

    limparRecibo();

    showToast("Formulario limpo.");
}

function atualizarAdicionaisDoQuarto() {
    const quartoId = Number(document.getElementById("quarto-alg").value);
    const quarto = quartosDaResidencia.find(item => Number(item.id) === quartoId);

    document.getElementById("algAr").checked = Boolean(quarto?.possuiAR);
    document.getElementById("algHidro").checked = Boolean(quarto?.possuiHidro);
}

function limparRecibo() {
    ultimoRecibo = null;

    [
        "recCliente",
        "recCpf",
        "recResidencia",
        "recQuarto",
        "recEntrada",
        "recSaida",
        "recDiarias",
        "recValor",
        "recAr",
        "recHidro",
        "recTotal"
    ].forEach(id => {
        document.getElementById(id).textContent = "-";
    });
}

function abrirReciboParaImpressao(modo) {
    if (!ultimoRecibo) {
        showToast("Selecione ou registre um aluguel para gerar o recibo.", "error");
        return;
    }

    const janela = window.open("", "_blank", "width=900,height=720");

    if (!janela) {
        showToast("Permita pop-ups para imprimir ou exportar o recibo.", "error");
        return;
    }

    janela.document.write(montarDocumentoRecibo(ultimoRecibo, modo));
    janela.document.close();
    janela.focus();

    setTimeout(() => {
        janela.print();
    }, 300);
}

function montarDocumentoRecibo(aluguel, modo) {
    const titulo = modo === "pdf" ? "Exportar PDF" : "Imprimir Recibo";

    const adicionais = [
        aluguel.quarto?.possuiAR ? "Ar-condicionado" : null,
        aluguel.quarto?.possuiHidro ? "Hidromassagem" : null
    ].filter(Boolean).join(", ") || "Sem adicionais";

    return `
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>${titulo} - Hospedagem Marau</title>
    <style>
        @page { size: A4; margin: 18mm; }

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
            color: #1e2d2f;
            background: #f5ede0;
        }

        .sheet {
            max-width: 760px;
            margin: 24px auto;
            background: #fffdf8;
            border: 1px solid #e7d4b8;
            border-radius: 10px;
            overflow: hidden;
        }

        .header {
            background: #1a6b7c;
            color: white;
            padding: 28px 34px;
        }

        .header h1 {
            margin: 0 0 6px;
            font-family: Georgia, "Times New Roman", serif;
            font-size: 28px;
        }

        .header p {
            margin: 0;
            opacity: .86;
        }

        .content {
            padding: 30px 34px;
        }

        .meta {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px 28px;
            margin-bottom: 26px;
        }

        .item {
            border-bottom: 1px solid #e7d4b8;
            padding: 10px 0;
        }

        .item .label {
            display: block;
            font-size: 11px;
            color: #5a6e70;
            text-transform: uppercase;
            letter-spacing: .6px;
            margin-bottom: 4px;
        }

        .item .value {
            font-size: 15px;
            font-weight: 700;
        }

        .total {
            margin-top: 28px;
            padding: 20px 24px;
            border-radius: 8px;
            background: #f5ede0;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .total span:first-child {
            font-size: 15px;
            font-weight: 700;
        }

        .total span:last-child {
            color: #1a6b7c;
            font-family: Georgia, "Times New Roman", serif;
            font-size: 30px;
            font-weight: 700;
        }

        .footer {
            padding-top: 30px;
            display: flex;
            justify-content: space-between;
            gap: 32px;
        }

        .assinatura {
            flex: 1;
            border-top: 1px solid #5a6e70;
            padding-top: 8px;
            text-align: center;
            color: #5a6e70;
            font-size: 12px;
        }

        @media print {
            body {
                background: white;
            }

            .sheet {
                margin: 0;
                border-radius: 0;
                border: none;
                max-width: none;
            }
        }
    </style>
</head>
<body>
    <main class="sheet">
        <section class="header">
            <h1>Hospedagem Marau</h1>
            <p>Formulario de aluguel - Recibo</p>
        </section>

        <section class="content">
            <div class="meta">
                ${linhaRecibo("Cliente", aluguel.cliente?.nome)}
                ${linhaRecibo("CPF", aluguel.cliente?.cpf)}
                ${linhaRecibo("Residencia", formatarResidencia(aluguel.residencia))}
                ${linhaRecibo("Quarto", aluguel.quarto?.id)}
                ${linhaRecibo("Entrada", formatarData(aluguel.dataEntrada))}
                ${linhaRecibo("Saida", formatarData(aluguel.dataSaida))}
                ${linhaRecibo("Numero de diarias", aluguel.qtdDiarias)}
                ${linhaRecibo("Adicionais", adicionais)}
            </div>

            <div class="total">
                <span>Total a pagar</span>
                <span>${moeda(aluguel.valorFinal)}</span>
            </div>

            <div class="footer">
                <div class="assinatura">Assinatura do cliente</div>
                <div class="assinatura">Responsavel pela hospedagem</div>
            </div>
        </section>
    </main>
</body>
</html>`;
}

function linhaRecibo(label, value) {
    return `
        <div class="item">
            <span class="label">${label}</span>
            <span class="value">${valorSeguro(value)}</span>
        </div>`;
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
