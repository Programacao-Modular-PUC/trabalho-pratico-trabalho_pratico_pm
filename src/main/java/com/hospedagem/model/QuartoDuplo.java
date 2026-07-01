package com.hospedagem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonTypeName;



@Getter
@Setter
@Entity
@DiscriminatorValue("DUPLO")

@JsonTypeName("DUPLO")
public class QuartoDuplo extends Quarto {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cama_casal")
    private TipoCamaCasal tipoCama = TipoCamaCasal.CASAL;

   @Column(name = "taxa_berco")
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
