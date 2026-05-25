package com.hospedagem.service;

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
                .orElseThrow(() -> new RuntimeException("Aluguel não encontrado: " + id));
    }

    public List<Aluguel> historicoPorResidencia(Long residenciaId) {
        return aluguelRepository.findByResidenciaId(residenciaId);
    }

    @Transactional
    public Aluguel realizarAluguel(Aluguel aluguel) {
        // Valida disponibilidade do quarto
        boolean disponivel = quartoRepository.isDisponivel(
                aluguel.getQuarto().getId(),
                aluguel.getDataEntrada(),
                aluguel.getDataSaida()
        );

        if (!disponivel) {
            throw new RuntimeException("Quarto indisponível no período solicitado.");
        }

        // Sinaliza se é reserva futura
        aluguel.setReservaFutura(aluguel.isReservaFutura());

        // Calcula diárias e valor final
        aluguel.calcularValorFinal();

        // Gera pagamento
        aluguel.gerarPagamento();

        return aluguelRepository.save(aluguel);
    }

    public String emitirFormulario(Long aluguelId) {
        Aluguel aluguel = buscarPorId(aluguelId);
        return aluguel.emitirFormulario();
    }
}
