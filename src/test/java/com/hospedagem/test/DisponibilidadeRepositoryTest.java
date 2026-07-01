package com.hospedagem.test;

import com.hospedagem.model.Aluguel;
import com.hospedagem.model.Cliente;
import com.hospedagem.model.QuartoIndividual;
import com.hospedagem.model.Residencia;
import com.hospedagem.repository.AluguelRepository;
import com.hospedagem.repository.ClienteRepository;
import com.hospedagem.repository.QuartoRepository;
import com.hospedagem.repository.ResidenciaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class DisponibilidadeRepositoryTest {

    @Autowired
    private QuartoRepository quartoRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AluguelRepository aluguelRepository;

    @Test
    @DisplayName("Aluguel cancelado nao bloqueia disponibilidade do quarto")
    void aluguelCanceladoNaoBloqueiaDisponibilidade() {
        QuartoIndividual quarto = criarQuartoPersistido();
        Aluguel cancelado = criarAluguel(quarto);
        cancelado.setCancelado(true);
        aluguelRepository.save(cancelado);

        boolean disponivel = quartoRepository.isDisponivel(
                quarto.getId(),
                LocalDateTime.of(2026, 7, 10, 12, 0),
                LocalDateTime.of(2026, 7, 12, 12, 0));

        assertTrue(disponivel);
    }

    @Test
    @DisplayName("Aluguel ativo bloqueia periodo sobreposto")
    void aluguelAtivoBloqueiaPeriodoSobreposto() {
        QuartoIndividual quarto = criarQuartoPersistido();
        aluguelRepository.save(criarAluguel(quarto));

        boolean disponivel = quartoRepository.isDisponivel(
                quarto.getId(),
                LocalDateTime.of(2026, 7, 11, 12, 0),
                LocalDateTime.of(2026, 7, 13, 12, 0));

        assertFalse(disponivel);
    }

    private QuartoIndividual criarQuartoPersistido() {
        Residencia residencia = new Residencia();
        residencia.setEndereco("Rua das Piscinas Naturais");
        residencia.setNumero("100");
        residencia.setBairro("Centro");
        residencia.setCep("45520-000");
        residencia.setTelefone("7333333333");
        residencia.setEmail("casa@marau.com");
        residencia = residenciaRepository.save(residencia);

        QuartoIndividual quarto = new QuartoIndividual();
        quarto.setValorBase(120.0);
        quarto.setResidencia(residencia);
        return quartoRepository.save(quarto);
    }

    private Aluguel criarAluguel(QuartoIndividual quarto) {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setCpf("12345678900");
        cliente.setEndereco("Rua A");
        cliente.setTelefone("31999999999");
        cliente.setEmail("cliente@teste.com");
        cliente = clienteRepository.save(cliente);

        Aluguel aluguel = new Aluguel();
        aluguel.setCliente(cliente);
        aluguel.setResidencia(quarto.getResidencia());
        aluguel.setQuarto(quarto);
        aluguel.setDataEntrada(LocalDateTime.of(2026, 7, 10, 12, 0));
        aluguel.setDataSaida(LocalDateTime.of(2026, 7, 12, 12, 0));
        aluguel.setQtdDiarias(2);
        aluguel.setValorFinal(240.0);
        return aluguel;
    }
}
