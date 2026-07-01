import { api } from "./api.js";

console.log("residencias.js carregou");

document.addEventListener("DOMContentLoaded", carregarResidencias);

async function carregarResidencias() {

    const lista = document.getElementById("listaResidencias");

    if (!lista) return;

    try {

        const residencias = await api.residencias.listar();
        console.log(JSON.stringify(residencias, null, 2));

        lista.innerHTML = "";

        residencias.forEach(residencia => {

            let quartosHTML = "";

            if (!residencia.quartos || residencia.quartos.length === 0) {

                quartosHTML = `
                    <div style="padding:20px;color:gray;">
                        Nenhum quarto cadastrado.
                    </div>
                `;

            } else {

residencia.quartos.forEach((quarto, index) => {
                    quartosHTML += `
                        <div class="quarto-card">

                            <div class="quarto-card-header">

                                <div>
                                  <div class="quarto-numero">
                                            Quarto ${index + 1}
                                        </div>

                                     <div class="quarto-tipo">
                                                ${quarto.tipo_quarto ?? quarto.tipoQuarto ?? ""}
                                            </div>

                                        <div style="font-size:14px;font-weight:600;color:var(--mar);margin-top:4px;">
                                            R$ ${quarto.valorBase.toFixed(2)}
                                        </div>

                                </div>

                                <span class="badge badge-green">
                                    Disponível
                                </span>

                            </div>

                            <div class="quarto-body">

                                <div class="quarto-amenities">

                                    ${
                                        quarto.possuiAR
                                        ? '<span class="amenity-tag">❄️ Ar-condicionado</span>'
                                        : ""
                                    }

                                    ${
                                        quarto.possuiHidro
                                        ? '<span class="amenity-tag">🛁 Hidromassagem</span>'
                                        : ""
                                    }

                                </div>

                            </div>

                        </div>
                    `;

                });

            }

            lista.innerHTML += `
                <div class="card">

                    <div class="card-header">

                        <div>

                            <h2 class="card-title">
                                🏡 ${residencia.endereco}
                            </h2>

                            <p style="font-size:13px;color:var(--texto-leve);margin-top:4px;">

                                Nº ${residencia.numero}
                                · Bairro ${residencia.bairro}
                                · CEP ${residencia.cep}
                                · ${residencia.telefone}
                                · ${residencia.email}

                            </p>

                        </div>

                        <div style="display:flex;gap:8px;">

                            <button
                                class="btn btn-ghost btn-sm"
                                onclick="editarResidencia(${residencia.id})">

                                ✏️ Editar

                            </button>

                            <button
                                class="btn btn-ghost btn-sm"
                                onclick="historicoResidencia(${residencia.id})">

                                📋 Histórico

                            </button>

                            <button
                                class="btn btn-ghost btn-sm"
                                onclick="deletarResidencia(${residencia.id})">

                                🗑️ Excluir

                            </button>

                        </div>

                    </div>

                    <p class="section-title">Quartos</p>

                    <div class="quartos-grid">

                        ${quartosHTML}

                    </div>

                </div>
            `;

        });

    } catch (e) {

        console.error(e);
        alert(e.message);

    }

}