package com.hospedagem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("INDIVIDUAL")
public class QuartoIndividual extends Quarto {

    @Column(name = "num_camas_solteiro", nullable = false)
    private int numCamasSolteiro = 1;

    public static final double ADICIONAL_POR_CAMA_EXTRA = 20.0;

    public int getCapacidadeMaxima() {
        return numCamasSolteiro;
    }

    @Override
    protected double calcularValorBase() {
        if (numCamasSolteiro <= 1) {
            return getValorBase();
        }
        return getValorBase() + (numCamasSolteiro - 1) * ADICIONAL_POR_CAMA_EXTRA;
    }
}
