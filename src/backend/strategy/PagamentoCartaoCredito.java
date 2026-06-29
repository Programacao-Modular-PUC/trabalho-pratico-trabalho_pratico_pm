package com.hospedagem.strategy;

import java.util.UUID;

public class PagamentoCartaoCredito implements MeioPagamento {

    private static final double TAXA_JUROS_POR_PARCELA = 0.0199;

    @Override
    public String getNome() {
        return "CARTAO_CREDITO";
    }

    @Override
    public boolean validar(DadosPagamento dados) {
        return dados != null
                && dados.getNumeroCartao() != null
                && dados.getNumeroCartao().replaceAll("\\D", "").length() == 16
                && dados.getNomeTitular() != null
                && !dados.getNomeTitular().isBlank()
                && dados.getParcelas() >= 1;
    }

    @Override
    public ResultadoPagamento processar(double valor) {
        String codigo = "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return ResultadoPagamento.sucesso(codigo, valor);
    }

    public double calcularValorParcelado(double valor, int parcelas) {
        if (parcelas <= 1) return valor;
        double fator = Math.pow(1 + TAXA_JUROS_POR_PARCELA, parcelas);
        return valor * fator;
    }
}
