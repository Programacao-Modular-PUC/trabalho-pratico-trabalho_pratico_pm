import { api } from "./api.js";
import { showToast } from "./toast.js";

document.addEventListener("DOMContentLoaded", iniciar);

async function iniciar(){

    await carregarResidencias();

    document
        .getElementById("btnSalvarQuarto")
        .addEventListener("click", salvarQuarto);

}

async function carregarResidencias(){

    const select = document.getElementById("residenciaQuarto");

    const residencias = await api.residencias.listar();

    select.innerHTML =
        '<option value="">Selecione a residência...</option>';

    residencias.forEach(r => {

        select.innerHTML += `
            <option value="${r.id}">
                ${r.endereco}, ${r.numero}
            </option>
        `;

    });

}

async function salvarQuarto() {

    const residenciaId = document.getElementById("residenciaQuarto").value;
    const tipo = document.getElementById("tipoQuarto").value;

    const quarto = {
        valorBase: Number(document.getElementById("valorQuarto").value),
        possuiAR: document.getElementById("arCondicionado").checked,
        possuiHidro: document.getElementById("hidromassagem").checked
    };

if (tipo === "Solteiro") {

    quarto.tipo_quarto = "INDIVIDUAL";
    quarto.numCamasSolteiro = 1;

} else if (tipo === "Casal") {

    quarto.tipo_quarto = "DUPLO";
    quarto.tipoCama = "CASAL";

} else if (tipo === "Familia") {

    quarto.tipo_quarto = "FAMILIA";
    quarto.numCamasSolteiro = 2;
    quarto.numCamasCasal = 1;
    quarto.numCamasEspeciais = 0;
    quarto.ambientes = "Único";

} else {

    showToast("Selecione um tipo de quarto.", "error");
    return;

}

try{

    console.log("Residência:", residenciaId);
    console.log("Quarto:", quarto);

    const resposta = await api.quartos.criar(residenciaId, quarto);

    console.log("Resposta:", resposta);

    showToast("Quarto cadastrado com sucesso!");

    location.href = "residencias.html";

}catch(e){

    console.error(e);

    showToast(e.message,"error");

}
}