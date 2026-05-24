package com.hospedagem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Quarto Individual (Solteiro):
 * - Pode ter 1 ou mais camas de solteiro.
 * - Não permite berço.
 * - Valor da diária aumenta com número de camas (valor base + adicional por cama extra).
 *   Se houver apenas 1 cama não tem adicional, apenas o valor base.
 * - Limite de hóspedes proporcional às camas (1 hóspede por cama).
 */
@Getter
@Setter
@Entity
@DiscriminatorValue("INDIVIDUAL")
public class QuartoIndividual extends Quarto {

    @Column(name = "num_camas_solteiro", nullable = false)
    private int numCamasSolteiro = 1;

    /** Adicional cobrado por cada cama extra além da primeira */
    public static final double ADICIONAL_POR_CAMA_EXTRA = 20.0;

    /**
     * Capacidade máxima: 1 hóspede por cama de solteiro.
     */
    public int getCapacidadeMaxima() {
        return numCamasSolteiro;
    }

    /**
     * Valor base do quarto individual:
     * - 1 cama: apenas valorBase
     * - N camas: valorBase + (N-1) * ADICIONAL_POR_CAMA_EXTRA
     */
    @Override
    protected double calcularValorBase() {
        if (numCamasSolteiro <= 1) {
            return getValorBase();
        }
        return getValorBase() + (numCamasSolteiro - 1) * ADICIONAL_POR_CAMA_EXTRA;
    }
}
