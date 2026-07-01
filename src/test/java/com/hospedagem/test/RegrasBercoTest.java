package com.hospedagem.test;
import com.hospedagem.model.Aluguel;
import com.hospedagem.model.QuartoIndividual;
import com.hospedagem.model.QuartoDuplo;
import com.hospedagem.model.TipoCamaCasal;


import com.hospedagem.exception.RecursoNaoPermitidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RegrasBercoTest {

    private QuartoDuplo quartoDuplo;
    private QuartoIndividual quartoIndividual;
    private Aluguel aluguel;

    @BeforeEach
    void setUp() {
        quartoDuplo = new QuartoDuplo();
        quartoDuplo.setValorBase(150.0);
        quartoDuplo.setTipoCama(TipoCamaCasal.CASAL);

        quartoIndividual = new QuartoIndividual();
        quartoIndividual.setValorBase(100.0);

        aluguel = new Aluguel();
        aluguel.setDataEntrada(LocalDateTime.now());
        aluguel.setDataSaida(LocalDateTime.now().plusDays(2));
    }

    @Test
    @DisplayName("Quarto duplo permite berço")
    void quartoDuploPermiteBerco() {
        assertTrue(quartoDuplo.permiteBerco());
    }

    @Test
    @DisplayName("Quarto individual não permite berço")
    void quartoIndividualNaoPermiteBerco() {
        assertFalse(quartoIndividual.permiteBerco());
    }

    @Test
    @DisplayName("Quarto duplo: diária com berço soma a taxa de berço")
    void diariaDuploComBerco() {
        double semBerco = quartoDuplo.calcularDiariaComBerco(false);
        double comBerco = quartoDuplo.calcularDiariaComBerco(true);
        assertEquals(semBerco + quartoDuplo.getTaxaBerco(), comBerco, 0.001);
    }

    @Test
    @DisplayName("Aluguel: solicitar berço em quarto que não permite lança RecursoNaoPermitidoException")
    void bercoEmQuartoIndividualLancaExcecao() {
        aluguel.setQuarto(quartoIndividual);
        aluguel.setSolicitouBerco(true);

        assertThrows(RecursoNaoPermitidoException.class, aluguel::validarBerco);
    }

    @Test
    @DisplayName("Aluguel: solicitar berço em quarto duplo é permitido")
    void bercoEmQuartoDuploPermitido() {
        aluguel.setQuarto(quartoDuplo);
        aluguel.setSolicitouBerco(true);

        assertDoesNotThrow(aluguel::validarBerco);
    }
}
