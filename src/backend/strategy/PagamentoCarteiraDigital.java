package com.hospedagem.strategy;

import java.util.Set;
import java.util.UUID;

public class PagamentoCarteiraDigital implements MeioPagamento {

    private static final Set<String> CARTEIRAS_SUPORTADAS = Set.of("PICPAY", "MERCADOPAGO", "PAGBANK");

    @Override
    public String getNome() {
        return "CARTEIRA_DIGITAL";
    }

    @Override
    public boolean validar(DadosPagamento dados) {
        return dados != null
                && dados.getCarteiraNome() != null
                && CARTEIRAS_SUPORTADAS.contains(dados.getCarteiraNome().toUpperCase());
    }

    @Override
    public ResultadoPagamento processar(double valor) {
        String codigo = "CW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return ResultadoPagamento.sucesso(codigo, valor);
    }

    public Set<String> getCarteirasSuportadas() {
        return CARTEIRAS_SUPORTADAS;
    }
}
