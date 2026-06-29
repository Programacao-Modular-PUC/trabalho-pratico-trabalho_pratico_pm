package com.hospedagem.strategy;

public interface MeioPagamento {

    String getNome();

    ResultadoPagamento processar(double valor);

    boolean validar(DadosPagamento dados);
}
