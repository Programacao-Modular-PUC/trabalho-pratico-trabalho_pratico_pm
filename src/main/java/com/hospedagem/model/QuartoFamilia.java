package com.hospedagem.model;

import com.hospedagem.exception.CapacidadeExcedidaException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonTypeName;

@Getter
@Setter
@Entity
@DiscriminatorValue("FAMILIA")

@JsonTypeName("FAMILIA")
public class QuartoFamilia extends Quarto {

    @Column(name = "num_camas_solteiro")
    private int numCamasSolteiro = 0;

    @Column(name = "num_camas_casal")
    private int numCamasCasal = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cama_especial")
    private TipoCamaCasal tipoCamaEspecial = TipoCamaCasal.CASAL;

    @Column(name = "num_camas_especiais")
    private int numCamasEspeciais = 0;

    @Column(name = "ambientes")
    private String ambientes;

    public static final double PERCENTUAL_POR_HOSPEDE = 0.08;

    public static final int LIMIAR_DESCONTO = 4;

    public static final double DESCONTO_POR_HOSPEDE_EXTRA = 0.05;

    public static final double DESCONTO_MAXIMO = 0.25;

    public int getCapacidadeMaxima() {
        return numCamasSolteiro + (numCamasCasal * 2) + (numCamasEspeciais * 2);
    }

    @Override
    protected double calcularValorBase() {
        return getValorBase();
    }

    public double calcularDiariaPorHospedes(int numHospedes) {
        if (numHospedes < 1) {
            throw new IllegalArgumentException("Número de hóspedes deve ser >= 1");
        }
        if (numHospedes > getCapacidadeMaxima()) {
            throw new CapacidadeExcedidaException(
                    "Número de hóspedes (" + numHospedes + ") excede a capacidade máxima do quarto ("
                            + getCapacidadeMaxima() + ")");
        }

        double valorComHospedes = getValorBase() * (1 + PERCENTUAL_POR_HOSPEDE * numHospedes);

        if (isPossuiAR()) valorComHospedes += ADICIONAL_AR;
        if (isPossuiHidro()) valorComHospedes += ADICIONAL_HIDRO;

        if (numHospedes >= LIMIAR_DESCONTO) {
            int hospedesExtras = numHospedes - LIMIAR_DESCONTO + 1;
            double desconto = Math.min(hospedesExtras * DESCONTO_POR_HOSPEDE_EXTRA, DESCONTO_MAXIMO);
            valorComHospedes = valorComHospedes * (1 - desconto);
        }

        return valorComHospedes;
    }
}
