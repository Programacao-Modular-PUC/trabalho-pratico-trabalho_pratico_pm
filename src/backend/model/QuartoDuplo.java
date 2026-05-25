package com.hospedagem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("DUPLO")
public class QuartoDuplo extends Quarto {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cama_casal", nullable = false)
    private TipoCamaCasal tipoCama = TipoCamaCasal.CASAL;

    public static final double TAXA_BERCO = 25.0;

    @Override
    protected double calcularValorBase() {
        return getValorBase() + tipoCama.getAdicionalConforto();
    }

    public double calcularDiariaComBerco(boolean solicitouBerco) {
        double valor = calcularDiaria();
        if (solicitouBerco) valor += TAXA_BERCO;
        return valor;
    }
}
