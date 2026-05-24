package com.hospedagem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Quarto Duplo (Casal):
 * - Voltado para casais.
 * - Possui cama casal comum, Queen ou King.
 * - Pode ter berço (opcional, conforme solicitação do cliente no aluguel).
 * - Adicional de berço cobrado se solicitado.
 * - Adicional por conforto: cama CASAL sem adicional, QUEEN tem adicional, KING tem outro adicional.
 */
@Getter
@Setter
@Entity
@DiscriminatorValue("DUPLO")
public class QuartoDuplo extends Quarto {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cama_casal", nullable = false)
    private TipoCamaCasal tipoCama = TipoCamaCasal.CASAL;

    /** Taxa extra cobrada quando o cliente solicita berço */
    public static final double TAXA_BERCO = 25.0;

    /**
     * Valor base do quarto duplo:
     * valorBase + adicional de conforto conforme tipo de cama.
     * O adicional de berço NÃO entra aqui — é calculado separadamente no aluguel.
     */
    @Override
    protected double calcularValorBase() {
        return getValorBase() + tipoCama.getAdicionalConforto();
    }

    /**
     * Calcula a diária considerando se o cliente solicitou berço.
     * @param solicitouBerco true se o cliente pediu berço
     */
    public double calcularDiariaComBerco(boolean solicitouBerco) {
        double valor = calcularDiaria();
        if (solicitouBerco) valor += TAXA_BERCO;
        return valor;
    }
}
