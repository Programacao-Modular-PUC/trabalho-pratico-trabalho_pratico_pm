package com.hospedagem.strategy;

import java.util.UUID;

public class PagamentoCartaoDebito implements MeioPagamento {

    private static final double DESCONTO_DEBITO = 0.02;

    @Override
    public String getNome() {
        return "CARTAO_DEBITO";
    }

    @Override
    public boolean validar(DadosPagamento dados) {
        return dados != null
                && dados.getNumeroCartao() != null
                && dados.getNumeroCartao().replaceAll("\\D", "").length() == 16
                && dados.getNomeTitular() != null
                && !dados.getNomeTitular().isBlank();
    }

    @Override
    public ResultadoPagamento processar(double valor) {
        double valorComDesconto = valor * (1 - DESCONTO_DEBITO);
        String codigo = "DB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return ResultadoPagamento.sucesso(codigo, valorComDesconto);
    }
}
