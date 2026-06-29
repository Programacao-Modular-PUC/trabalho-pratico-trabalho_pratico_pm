package com.hospedagem.model;

public enum TipoQuarto {

    INDIVIDUAL("INDIVIDUAL", QuartoIndividual.class),
    DUPLO("DUPLO", QuartoDuplo.class),
    FAMILIA("FAMILIA", QuartoFamilia.class);

    private final String discriminador;
    private final Class<? extends Quarto> classe;

    TipoQuarto(String discriminador, Class<? extends Quarto> classe) {
        this.discriminador = discriminador;
        this.classe = classe;
    }

    public String getDiscriminador() {
        return discriminador;
    }

    public Class<? extends Quarto> getClasse() {
        return classe;
    }

    public static TipoQuarto fromString(String valor) {
        for (TipoQuarto tipo : values()) {
            if (tipo.discriminador.equalsIgnoreCase(valor.trim())) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de quarto inválido: " + valor);
    }
}
