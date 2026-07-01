import { api } from "./api.js";

document.addEventListener("DOMContentLoaded", carregarClientes);

async function carregarClientes() {

    const tabela = document.getElementById("tabelaClientes");

    try {

        const clientes = await api.clientes.listar();

        tabela.innerHTML = "";

        clientes.forEach(cliente => {

            tabela.innerHTML += `
                <tr>

                    <td><strong>${cliente.nome}</strong></td>
                    <td>${cliente.cpf}</td>
                    <td>${cliente.telefone}</td>
                    <td>${cliente.email}</td>
                    <td>${cliente.endereco}</td>
                    <td>0</td>

                    <td style="display:flex;gap:6px">

                        <button class="btn btn-ghost btn-sm"
                            onclick="editarCliente(${cliente.id})">
                            ✏️ Editar
                        </button>

                        <button class="btn btn-ghost btn-sm"
                            onclick="historicoCliente(${cliente.id})">
                            📋 Histórico
                        </button>

                        <button class="btn btn-ghost btn-sm"
                            onclick="deletarCliente(${cliente.id})">
                            🗑️ Excluir
                        </button>

                    </td>

                </tr>
            `;

        });

    } catch (e) {

        console.error(e);
        alert(e.message);

    }

}

window.deletarCliente = async function(id){

    if(!confirm("Deseja excluir este cliente?"))
        return;

    try{

        await api.clientes.deletar(id);

        carregarClientes();

        alert("Cliente removido!");

    }catch(e){

        alert(e.message);

    }

}

window.editarCliente = async function(id){

    try{

        const cliente = await api.clientes.buscar(id);

        const nome = prompt("Nome",cliente.nome);
        if(nome==null) return;

        const telefone = prompt("Telefone",cliente.telefone);
        if(telefone==null) return;

        const email = prompt("Email",cliente.email);
        if(email==null) return;

        const endereco = prompt("Endereço",cliente.endereco);
        if(endereco==null) return;

        cliente.nome = nome;
        cliente.telefone = telefone;
        cliente.email = email;
        cliente.endereco = endereco;

        await api.clientes.atualizar(id,cliente);

        carregarClientes();

        alert("Cliente atualizado!");

    }catch(e){

        alert(e.message);

    }

}

window.historicoCliente = async function(id){

    try{

        const historico = await api.get(`/clientes/${id}/historico`);

        if(historico.length===0){

            alert("Este cliente ainda não possui hospedagens.");

            return;

        }

        let texto="Histórico de hospedagens\n\n";

        historico.forEach(h=>{

            texto +=
`Quarto: ${h.quarto.id}
Entrada: ${h.dataEntrada}
Saída: ${h.dataSaida}

`;

        });

        alert(texto);

    }catch(e){

        alert(e.message);

    }

}