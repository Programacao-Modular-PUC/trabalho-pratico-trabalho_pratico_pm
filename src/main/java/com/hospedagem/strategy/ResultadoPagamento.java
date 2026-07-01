package com.hospedagem.strategy;

public class ResultadoPagamento {

    private final boolean sucesso;
    private final String codigoTransacao;
    private final String mensagem;
    private final double valorProcessado;

    public ResultadoPagamento(boolean sucesso, String codigoTransacao, String mensagem, double valorProcessado) {
        this.sucesso = sucesso;
        this.codigoTransacao = codigoTransacao;
        this.mensagem = mensagem;
        this.valorProcessado = valorProcessado;
    }

    public static ResultadoPagamento sucesso(String codigo, double valor) {
        return new ResultadoPagamento(true, codigo, "Pagamento processado com sucesso.", valor);
    }

    public static ResultadoPagamento falha(String motivo) {
        return new ResultadoPagamento(false, null, motivo, 0.0);
    }

    public boolean isSucesso() { return sucesso; }
    public String getCodigoTransacao() { return codigoTransacao; }
    public String getMensagem() { return mensagem; }
    public double getValorProcessado() { return valorProcessado; }
}
