package com.hospedagem.strategy;

import java.util.UUID;

public class PagamentoDinheiro implements MeioPagamento {

    @Override
    public String getNome() {
        return "DINHEIRO";
    }

    @Override
    public boolean validar(DadosPagamento dados) {
        return true;
    }

    @Override
    public ResultadoPagamento processar(double valor) {
        String codigo = "DIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return ResultadoPagamento.sucesso(codigo, valor);
    }
}
