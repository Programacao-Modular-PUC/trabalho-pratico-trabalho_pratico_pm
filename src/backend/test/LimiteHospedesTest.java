package com.hospedagem.model;

import com.hospedagem.exception.CapacidadeExcedidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LimiteHospedesTest {

    private QuartoFamilia quartoFamilia;
    private QuartoIndividual quartoIndividual;

    @BeforeEach
    void setUp() {
        quartoFamilia = new QuartoFamilia();
        quartoFamilia.setValorBase(200.0);
        quartoFamilia.setNumCamasSolteiro(1);
        quartoFamilia.setNumCamasCasal(1);
        quartoFamilia.setNumCamasEspeciais(0);

        quartoIndividual = new QuartoIndividual();
        quartoIndividual.setValorBase(100.0);
        quartoIndividual.setNumCamasSolteiro(2);
    }

    @Test
    @DisplayName("Capacidade máxima do quarto família soma camas solteiro, casal e especiais")
    void capacidadeMaximaFamilia() {
        assertEquals(3, quartoFamilia.getCapacidadeMaxima());
    }

    @Test
    @DisplayName("Capacidade máxima do quarto individual é o número de camas de solteiro")
    void capacidadeMaximaIndividual() {
        assertEquals(2, quartoIndividual.getCapacidadeMaxima());
    }

    @Test
    @DisplayName("Número de hóspedes dentro do limite não lança exceção")
    void hospedesDentroDoLimite() {
        assertDoesNotThrow(() -> quartoFamilia.calcularDiariaPorHospedes(quartoFamilia.getCapacidadeMaxima()));
    }

    @Test
    @DisplayName("Número de hóspedes acima do limite lança CapacidadeExcedidaException")
    void hospedesAcimaDoLimite() {
        int acimaDoLimite = quartoFamilia.getCapacidadeMaxima() + 1;
        assertThrows(CapacidadeExcedidaException.class,
                () -> quartoFamilia.calcularDiariaPorHospedes(acimaDoLimite));
    }
}
