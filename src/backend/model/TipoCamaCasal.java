package com.hospedagem.model;

/**
 * Tipo de cama disponível no quarto de casal.
 * Cada tipo tem um adicional de conforto diferente.
 */
public enum TipoCamaCasal {
    CASAL(0.0),
    QUEEN(40.0),
    KING(70.0);

    private final double adicionalConforto;

    TipoCamaCasal(double adicionalConforto) {
        this.adicionalConforto = adicionalConforto;
    }

    public double getAdicionalConforto() {
        return adicionalConforto;
    }
}
