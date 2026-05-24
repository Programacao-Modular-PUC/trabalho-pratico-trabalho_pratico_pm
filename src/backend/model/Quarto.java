package com.hospedagem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe abstrata base para todos os tipos de quarto.
 * Atributos comuns: id, valorBase, possuiAR, possuiHidro
 */
@Getter
@Setter
@Entity
@Table(name = "quartos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_quarto", discriminatorType = DiscriminatorType.STRING)
public abstract class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_base", nullable = false)
    private double valorBase;

    @Column(name = "possui_ar")
    private boolean possuiAR;

    @Column(name = "possui_hidro")
    private boolean possuiHidro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residencia_id")
    private Residencia residencia;

    /** Taxa adicional por ar condicionado */
    public static final double ADICIONAL_AR = 30.0;

    /** Taxa adicional por hidromassagem */
    public static final double ADICIONAL_HIDRO = 50.0;

    /**
     * Calcula o valor da diária considerando o tipo e os adicionais de AR e Hidro.
     * Cada subclasse implementa sua própria regra de cálculo do valor base.
     */
    public double calcularDiaria() {
        double valor = calcularValorBase();
        if (possuiAR) valor += ADICIONAL_AR;
        if (possuiHidro) valor += ADICIONAL_HIDRO;
        return valor;
    }

    /**
     * Cálculo do valor base específico por tipo de quarto.
     * Sobrescrito pelas subclasses.
     */
    protected abstract double calcularValorBase();
}
