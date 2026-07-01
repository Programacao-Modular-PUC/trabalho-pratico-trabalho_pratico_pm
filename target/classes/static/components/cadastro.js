import { api } from "./api.js";
import { showToast } from "./toast.js";

document.addEventListener("DOMContentLoaded", () => {

    const btn = document.getElementById("btnSalvarCliente");

    if (!btn) return;

    btn.addEventListener("click", async () => {

        const endereco =
            `${document.getElementById("rua").value}, ` +
            `${document.getElementById("numero").value}, ` +
            `${document.getElementById("bairro").value}, ` +
            `${document.getElementById("cidade").value} - ` +
            `${document.getElementById("estado").value}, CEP: ` +
            `${document.getElementById("cep").value}`;

        const cliente = {
            nome: document.getElementById("nome").value,
            cpf: document.getElementById("cpf").value,
            telefone: document.getElementById("telefone").value,
            email: document.getElementById("email").value,
            endereco: endereco
        };

        try {

            await api.clientes.criar(cliente);

            showToast("Cliente cadastrado com sucesso!");

            setTimeout(() => {
            window.location.href = "/clientes.html";
        }, 1000);

        } catch (e) {

            showToast(e.message, "error");

            console.error(e);

        }

    });

});

const btnResidencia = document.getElementById("btnSalvarResidencia");

if (btnResidencia) {

    btnResidencia.addEventListener("click", async () => {

   const residencia = {

    endereco: document.getElementById("resEndereco").value,

    numero: document.getElementById("resNumero").value,

    bairro: document.getElementById("resBairro").value,

    cep: document.getElementById("resCep").value,

    telefone: document.getElementById("resTelefone").value,

    email: document.getElementById("resEmail").value,

    nome: document.getElementById("resNome").value

};

        try {

            await api.residencias.criar(residencia);

            showToast("Residência cadastrada com sucesso!");

            setTimeout(() => {

                window.location.href = "/residencias.html";

            }, 1000);

        } catch (e) {

            showToast(e.message, "error");

            console.error(e);

        }

    });

}