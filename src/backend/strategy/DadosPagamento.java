package com.hospedagem.strategy;

public class DadosPagamento {

    private String chavePix;
    private String numeroCartao;
    private String nomeTitular;
    private int parcelas;
    private String carteiraNome;

    public DadosPagamento() {
    }

    public String getChavePix() { return chavePix; }
    public void setChavePix(String chavePix) { this.chavePix = chavePix; }

    public String getNumeroCartao() { return numeroCartao; }
    public void setNumeroCartao(String numeroCartao) { this.numeroCartao = numeroCartao; }

    public String getNomeTitular() { return nomeTitular; }
    public void setNomeTitular(String nomeTitular) { this.nomeTitular = nomeTitular; }

    public int getParcelas() { return parcelas; }
    public void setParcelas(int parcelas) { this.parcelas = parcelas; }

    public String getCarteiraNome() { return carteiraNome; }
    public void setCarteiraNome(String carteiraNome) { this.carteiraNome = carteiraNome; }
}
