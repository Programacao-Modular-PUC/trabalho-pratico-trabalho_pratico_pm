package com.hospedagem.test;

import com.hospedagem.exception.EntidadeNaoEncontradaException;
import com.hospedagem.exception.QuartoIndisponivelException;
import com.hospedagem.exception.RecursoNaoPermitidoException;
import com.hospedagem.model.*;
import com.hospedagem.repository.AluguelRepository;
import com.hospedagem.repository.QuartoRepository;
import com.hospedagem.repository.ResidenciaRepository;
import com.hospedagem.observer.CentralNotificacoes;
import com.hospedagem.strategy.ProcessadorPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import com.hospedagem.service.ClienteService;
import com.hospedagem.service.ResidenciaService;
import com.hospedagem.service.QuartoService;
import com.hospedagem.service.AluguelService;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisponibilidadeTest {

    @Mock
    private QuartoRepository quartoRepository;

    @Mock
    private ResidenciaRepository residenciaRepository;

    @Mock
    private AluguelRepository aluguelRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ResidenciaService residenciaService;

    @Mock
    private CentralNotificacoes centralNotificacoes;

    @Mock
    private ProcessadorPagamento processadorPagamento;

    @InjectMocks
    private QuartoService quartoService;

    @InjectMocks
    private AluguelService aluguelService;

    private QuartoIndividual quarto;
    private Aluguel aluguel;

    @BeforeEach
    void setUp() {
        quarto = new QuartoIndividual();
        quarto.setId(1L);
        quarto.setValorBase(100.0);

        aluguel = new Aluguel();
        aluguel.setQuarto(quarto);
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");
        cliente.setEmail("cliente@teste.com");
        cliente.setTelefone("31999999999");

        Residencia residencia = new Residencia();
        residencia.setId(1L);
        quarto.setResidencia(residencia);

        aluguel.setCliente(cliente);
        aluguel.setResidencia(residencia);
        aluguel.setDataEntrada(LocalDateTime.now());
        aluguel.setDataSaida(LocalDateTime.now().plusDays(2));
    }

    @Test
    @DisplayName("QuartoService retorna true quando o repositório indica disponibilidade")
    void quartoDisponivel() {
        when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
        when(quartoRepository.isDisponivel(eq(1L), any(), any())).thenReturn(true);

        boolean resultado = quartoService.verificarDisponibilidade(
                1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertTrue(resultado);
    }

    @Test
    @DisplayName("QuartoService lança EntidadeNaoEncontradaException para quarto inexistente")
    void quartoInexistente() {
        when(quartoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class,
                () -> quartoService.verificarDisponibilidade(99L, LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
    }

    @Test
    @DisplayName("AluguelService lança QuartoIndisponivelException quando quarto está ocupado no período")
    void aluguelComQuartoIndisponivel() {
        when(clienteService.buscarPorId(1L)).thenReturn(aluguel.getCliente());
        when(residenciaService.buscarPorId(1L)).thenReturn(aluguel.getResidencia());
        when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
        when(quartoRepository.isDisponivel(eq(1L), any(), any())).thenReturn(false);

        assertThrows(QuartoIndisponivelException.class, () -> aluguelService.realizarAluguel(aluguel));

        verify(aluguelRepository, never()).save(any());
    }

    @Test
    @DisplayName("AluguelService realiza o aluguel quando o quarto está disponível")
    void aluguelComQuartoDisponivel() {
        when(clienteService.buscarPorId(1L)).thenReturn(aluguel.getCliente());
        when(residenciaService.buscarPorId(1L)).thenReturn(aluguel.getResidencia());
        when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
        when(quartoRepository.isDisponivel(eq(1L), any(), any())).thenReturn(true);
        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Aluguel resultado = aluguelService.realizarAluguel(aluguel);

        assertNotNull(resultado);
        assertTrue(resultado.getValorFinal() > 0);
        verify(aluguelRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("AluguelService rejeita quarto que nao pertence a residencia informada")
    void aluguelComQuartoDeOutraResidencia() {
        Residencia outraResidencia = new Residencia();
        outraResidencia.setId(2L);
        quarto.setResidencia(outraResidencia);

        when(clienteService.buscarPorId(1L)).thenReturn(aluguel.getCliente());
        when(residenciaService.buscarPorId(1L)).thenReturn(aluguel.getResidencia());
        when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));

        assertThrows(RecursoNaoPermitidoException.class, () -> aluguelService.realizarAluguel(aluguel));
        verify(aluguelRepository, never()).save(any());
    }
}
