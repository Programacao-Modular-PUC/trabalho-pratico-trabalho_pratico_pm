package com.hospedagem.model;

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
