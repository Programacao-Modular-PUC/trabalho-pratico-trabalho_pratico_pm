package com.hospedagem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    public static final double ADICIONAL_AR = 30.0;

    public static final double ADICIONAL_HIDRO = 50.0;

    public double calcularDiaria() {
        double valor = calcularValorBase();
        if (possuiAR) valor += ADICIONAL_AR;
        if (possuiHidro) valor += ADICIONAL_HIDRO;
        return valor;
    }

    protected abstract double calcularValorBase();

    public boolean permiteBerco() {
        return false;
    }
}
