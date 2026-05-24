package com.hospedagem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Quarto Família:
 * - Capacidade maior com mistura de camas (solteiro, casal, queen/king).
 * - Capacidade máxima conforme configuração das camas.
 * - Possui ambientes distintos (ex: home office, área de estudo).
 * - Cálculo por número de hóspedes (não por camas).
 *   Regra: valorBase * (1 + percentualPorHospede * numHospedes)
 * - Desconto progressivo para grupos:
 *   Regra: a partir de 4 hóspedes, desconto de 5% por hóspede adicional (max 25%).
 */
@Getter
@Setter
@Entity
@DiscriminatorValue("FAMILIA")
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

    /** Ambientes extras do quarto (ex: "home office, área de estudo") */
    @Column(name = "ambientes")
    private String ambientes;

    /**
     * Percentual adicional por hóspede no cálculo da diária.
     * Ex: 0.10 = 10% a mais por hóspede acima de 1.
     */
    public static final double PERCENTUAL_POR_HOSPEDE = 0.08;

    /**
     * Limiar de hóspedes a partir do qual incide desconto progressivo.
     */
    public static final int LIMIAR_DESCONTO = 4;

    /**
     * Percentual de desconto por hóspede acima do limiar.
     */
    public static final double DESCONTO_POR_HOSPEDE_EXTRA = 0.05;

    /**
     * Desconto máximo permitido (25%).
     */
    public static final double DESCONTO_MAXIMO = 0.25;

    /**
     * Capacidade máxima: 1 por cama solteiro, 2 por cama casal/queen/king.
     */
    public int getCapacidadeMaxima() {
        return numCamasSolteiro + (numCamasCasal * 2) + (numCamasEspeciais * 2);
    }

    /**
     * Cálculo do valor base — não depende de hóspedes, apenas da configuração do quarto.
     * O cálculo real por hóspedes é feito em calcularDiariaPorHospedes().
     */
    @Override
    protected double calcularValorBase() {
        return getValorBase();
    }

    /**
     * Calcula a diária para um número específico de hóspedes.
     * Regra: valorBase * (1 + PERCENTUAL_POR_HOSPEDE * numHospedes)
     * Com desconto progressivo para grupos: a partir de LIMIAR_DESCONTO hóspedes,
     * desconto de DESCONTO_POR_HOSPEDE_EXTRA por hóspede adicional (max DESCONTO_MAXIMO).
     *
     * @param numHospedes número de hóspedes que irão se hospedar
     * @return valor da diária para o grupo
     */
    public double calcularDiariaPorHospedes(int numHospedes) {
        if (numHospedes < 1) throw new IllegalArgumentException("Número de hóspedes deve ser >= 1");
        if (numHospedes > getCapacidadeMaxima())
            throw new IllegalArgumentException("Número de hóspedes excede a capacidade máxima do quarto");

        // Valor proporcional ao número de hóspedes
        double valorComHospedes = getValorBase() * (1 + PERCENTUAL_POR_HOSPEDE * numHospedes);

        // Adicionar AR e Hidro
        if (isPossuiAR()) valorComHospedes += ADICIONAL_AR;
        if (isPossuiHidro()) valorComHospedes += ADICIONAL_HIDRO;

        // Aplicar desconto progressivo para grupos
        if (numHospedes >= LIMIAR_DESCONTO) {
            int hospedesExtras = numHospedes - LIMIAR_DESCONTO + 1;
            double desconto = Math.min(hospedesExtras * DESCONTO_POR_HOSPEDE_EXTRA, DESCONTO_MAXIMO);
            valorComHospedes = valorComHospedes * (1 - desconto);
        }

        return valorComHospedes;
    }
}
