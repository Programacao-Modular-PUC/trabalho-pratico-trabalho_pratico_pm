package com.hospedagem.service;

import com.hospedagem.exception.EntidadeNaoEncontradaException;
import com.hospedagem.exception.QuartoIndisponivelException;
import com.hospedagem.model.*;
import com.hospedagem.repository.AluguelRepository;
import com.hospedagem.repository.QuartoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final QuartoRepository quartoRepository;
    private final ClienteService clienteService;
    private final ResidenciaService residenciaService;

    public AluguelService(AluguelRepository aluguelRepository,
                          QuartoRepository quartoRepository,
                          ClienteService clienteService,
                          ResidenciaService residenciaService) {
        this.aluguelRepository = aluguelRepository;
        this.quartoRepository = quartoRepository;
        this.clienteService = clienteService;
        this.residenciaService = residenciaService;
    }

    public List<Aluguel> listarTodos() {
        return aluguelRepository.findAll();
    }

    public Aluguel buscarPorId(Long id) {
        return aluguelRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluguel não encontrado: " + id));
    }

    public List<Aluguel> historicoPorResidencia(Long residenciaId) {
        return aluguelRepository.findByResidenciaId(residenciaId);
    }

    public List<Aluguel> historicoPorCliente(Long clienteId) {
        clienteService.buscarPorId(clienteId);
        return aluguelRepository.findByClienteId(clienteId);
    }

    @Transactional
    public Aluguel realizarAluguel(Aluguel aluguel) {
        aluguel.validarDatas();
        aluguel.validarBerco();

        boolean disponivel = quartoRepository.isDisponivel(
                aluguel.getQuarto().getId(),
                aluguel.getDataEntrada(),
                aluguel.getDataSaida()
        );

        if (!disponivel) {
            throw new QuartoIndisponivelException("Quarto indisponível no período solicitado.");
        }

        aluguel.setReservaFutura(aluguel.isReservaFutura());

        aluguel.calcularValorFinal();

        aluguel.gerarPagamento();

        return aluguelRepository.save(aluguel);
    }

    @Transactional
    public void cancelarAluguel(Long id) {
        Aluguel aluguel = buscarPorId(id);
        aluguel.setCancelado(true);
        aluguelRepository.save(aluguel);
    }

    public String emitirFormulario(Long aluguelId) {
        Aluguel aluguel = buscarPorId(aluguelId);
        return aluguel.emitirFormulario();
    }
}
