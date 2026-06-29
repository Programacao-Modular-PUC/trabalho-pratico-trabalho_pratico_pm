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

    @Column(name = "taxa_berco", nullable = false)
    private double taxaBerco = 25.0;

    @Override
    protected double calcularValorBase() {
        return getValorBase() + tipoCama.getAdicionalConforto();
    }

    @Override
    public boolean permiteBerco() {
        return true;
    }

    public double calcularDiariaComBerco(boolean solicitouBerco) {
        double valor = calcularDiaria();
        if (solicitouBerco) valor += taxaBerco;
        return valor;
    }
}
