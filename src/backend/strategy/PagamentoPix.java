package com.hospedagem.strategy;

import java.util.UUID;

public class PagamentoPix implements MeioPagamento {

    @Override
    public String getNome() {
        return "PIX";
    }

    @Override
    public boolean validar(DadosPagamento dados) {
        return dados != null && dados.getChavePix() != null && !dados.getChavePix().isBlank();
    }

    @Override
    public ResultadoPagamento processar(double valor) {
        String codigo = "PIX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return ResultadoPagamento.sucesso(codigo, valor);
    }
}
