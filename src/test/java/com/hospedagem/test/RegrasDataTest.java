package com.hospedagem.test;
import com.hospedagem.model.Aluguel;
import com.hospedagem.model.QuartoIndividual;

import com.hospedagem.exception.DataInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RegrasDataTest {

    private Aluguel aluguel;

    @BeforeEach
    void setUp() {
        aluguel = new Aluguel();
        QuartoIndividual quarto = new QuartoIndividual();
        quarto.setValorBase(100.0);
        aluguel.setQuarto(quarto);
    }

    @Test
    @DisplayName("Data de saída anterior à de entrada lança DataInvalidaException")
    void dataSaidaAnteriorEntrada() {
        aluguel.setDataEntrada(LocalDateTime.now());
        aluguel.setDataSaida(LocalDateTime.now().minusDays(1));

        assertThrows(DataInvalidaException.class, aluguel::validarDatas);
    }

    @Test
    @DisplayName("Data de saída igual à data de entrada lança DataInvalidaException")
    void dataSaidaIgualEntrada() {
        LocalDateTime data = LocalDateTime.now();
        aluguel.setDataEntrada(data);
        aluguel.setDataSaida(data);

        assertThrows(DataInvalidaException.class, aluguel::validarDatas);
    }

    @Test
    @DisplayName("Datas nulas lançam DataInvalidaException")
    void datasNulas() {
        aluguel.setDataEntrada(null);
        aluguel.setDataSaida(null);

        assertThrows(DataInvalidaException.class, aluguel::validarDatas);
    }

    @Test
    @DisplayName("Datas válidas não lançam exceção")
    void datasValidas() {
        aluguel.setDataEntrada(LocalDateTime.now());
        aluguel.setDataSaida(LocalDateTime.now().plusDays(3));

        assertDoesNotThrow(aluguel::validarDatas);
    }

    @Test
    @DisplayName("Cálculo de diárias considera checkout após meio-dia como dia extra")
    void calculoDiariasComCheckoutAposMeioDia() {
        aluguel.setDataEntrada(LocalDateTime.of(2026, 6, 10, 14, 0));
        aluguel.setDataSaida(LocalDateTime.of(2026, 6, 12, 13, 0));

        assertEquals(3, aluguel.calcularDiarias());
    }

    @Test
    @DisplayName("Cálculo de diárias considera checkout até meio-dia sem dia extra")
    void calculoDiariasComCheckoutAteMeioDia() {
        aluguel.setDataEntrada(LocalDateTime.of(2026, 6, 10, 14, 0));
        aluguel.setDataSaida(LocalDateTime.of(2026, 6, 12, 11, 0));

        assertEquals(2, aluguel.calcularDiarias());
    }
}
