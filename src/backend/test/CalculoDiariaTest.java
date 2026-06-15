package com.hospedagem.model;

import com.hospedagem.exception.CapacidadeExcedidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculoDiariaTest {

    private QuartoIndividual quartoIndividual;
    private QuartoDuplo quartoDuplo;
    private QuartoFamilia quartoFamilia;

    @BeforeEach
    void setUp() {
        quartoIndividual = new QuartoIndividual();
        quartoIndividual.setValorBase(100.0);

        quartoDuplo = new QuartoDuplo();
        quartoDuplo.setValorBase(150.0);
        quartoDuplo.setTipoCama(TipoCamaCasal.CASAL);

        quartoFamilia = new QuartoFamilia();
        quartoFamilia.setValorBase(200.0);
        quartoFamilia.setNumCamasSolteiro(2);
        quartoFamilia.setNumCamasCasal(1);
    }

    @Test
    @DisplayName("Quarto individual: diária deve ser igual ao valor base sem adicionais")
    void diariaIndividualSemAdicionais() {
        assertEquals(100.0, quartoIndividual.calcularDiaria(), 0.001);
    }

    @Test
    @DisplayName("Quarto individual: cama extra acrescenta valor adicional")
    void diariaIndividualComCamaExtra() {
        quartoIndividual.setNumCamasSolteiro(2);
        double esperado = 100.0 + QuartoIndividual.ADICIONAL_POR_CAMA_EXTRA;
        assertEquals(esperado, quartoIndividual.calcularDiaria(), 0.001);
    }

    @Test
    @DisplayName("Quarto individual: AR e hidro somam adicionais à diária")
    void diariaIndividualComArEHidro() {
        quartoIndividual.setPossuiAR(true);
        quartoIndividual.setPossuiHidro(true);
        double esperado = 100.0 + Quarto.ADICIONAL_AR + Quarto.ADICIONAL_HIDRO;
        assertEquals(esperado, quartoIndividual.calcularDiaria(), 0.001);
    }

    @Test
    @DisplayName("Quarto duplo: diária considera adicional do tipo de cama de casal")
    void diariaDuploComTipoCamaKing() {
        quartoDuplo.setTipoCama(TipoCamaCasal.KING);
        double esperado = 150.0 + TipoCamaCasal.KING.getAdicionalConforto();
        assertEquals(esperado, quartoDuplo.calcularDiaria(), 0.001);
    }

    @Test
    @DisplayName("Quarto família: diária aumenta proporcionalmente ao número de hóspedes")
    void diariaFamiliaPorHospedes() {
        double valorComDoisHospedes = quartoFamilia.calcularDiariaPorHospedes(2);
        double esperado = 200.0 * (1 + QuartoFamilia.PERCENTUAL_POR_HOSPEDE * 2);
        assertEquals(esperado, valorComDoisHospedes, 0.001);
    }

    @Test
    @DisplayName("Quarto família: desconto é aplicado a partir do limiar de hóspedes")
    void diariaFamiliaComDesconto() {
        quartoFamilia.setNumCamasSolteiro(0);
        quartoFamilia.setNumCamasCasal(2);
        quartoFamilia.setNumCamasEspeciais(0);

        double valor = quartoFamilia.calcularDiariaPorHospedes(4);
        double base = 200.0 * (1 + QuartoFamilia.PERCENTUAL_POR_HOSPEDE * 4);
        double desconto = Math.min(1 * QuartoFamilia.DESCONTO_POR_HOSPEDE_EXTRA, QuartoFamilia.DESCONTO_MAXIMO);
        double esperado = base * (1 - desconto);

        assertEquals(esperado, valor, 0.001);
    }

    @Test
    @DisplayName("Quarto família: lança CapacidadeExcedidaException quando hóspedes excedem capacidade")
    void diariaFamiliaCapacidadeExcedida() {
        int capacidade = quartoFamilia.getCapacidadeMaxima();
        assertThrows(CapacidadeExcedidaException.class,
                () -> quartoFamilia.calcularDiariaPorHospedes(capacidade + 1));
    }

    @Test
    @DisplayName("Quarto família: lança IllegalArgumentException para número de hóspedes inválido")
    void diariaFamiliaHospedesInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> quartoFamilia.calcularDiariaPorHospedes(0));
    }
}
