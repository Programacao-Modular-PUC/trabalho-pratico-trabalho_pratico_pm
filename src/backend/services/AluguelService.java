package com.hospedagem.service;

import com.hospedagem.exception.EntidadeNaoEncontradaException;
import com.hospedagem.exception.QuartoIndisponivelException;
import com.hospedagem.model.Aluguel;
import com.hospedagem.observer.CentralNotificacoes;
import com.hospedagem.observer.ContextoNotificacao;
import com.hospedagem.observer.EventoHospedagem;
import com.hospedagem.repository.AluguelRepository;
import com.hospedagem.repository.QuartoRepository;
import com.hospedagem.singleton.GerenciadorLogs;
import com.hospedagem.strategy.DadosPagamento;
import com.hospedagem.strategy.ProcessadorPagamento;
import com.hospedagem.strategy.ResultadoPagamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final QuartoRepository quartoRepository;
    private final ClienteService clienteService;
    private final ResidenciaService residenciaService;
    private final CentralNotificacoes centralNotificacoes;
    private final ProcessadorPagamento processadorPagamento;

    public AluguelService(AluguelRepository aluguelRepository,
                          QuartoRepository quartoRepository,
                          ClienteService clienteService,
                          ResidenciaService residenciaService,
                          CentralNotificacoes centralNotificacoes,
                          ProcessadorPagamento processadorPagamento) {
        this.aluguelRepository = aluguelRepository;
        this.quartoRepository = quartoRepository;
        this.clienteService = clienteService;
        this.residenciaService = residenciaService;
        this.centralNotificacoes = centralNotificacoes;
        this.processadorPagamento = processadorPagamento;
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

        Aluguel salvo = aluguelRepository.save(aluguel);

        GerenciadorLogs.getInstance().info(
                "Aluguel #" + salvo.getId() + " criado para cliente "
                + salvo.getCliente().getNome()
                + " | Valor: R$ " + String.format("%.2f", salvo.getValorFinal()));

        centralNotificacoes.publicar(new ContextoNotificacao(
                EventoHospedagem.RESERVA_CRIADA,
                salvo.getId(),
                salvo.getCliente().getNome(),
                salvo.getCliente().getEmail(),
                salvo.getCliente().getTelefone(),
                "Entrada: " + salvo.getDataEntrada() + " | Saída: " + salvo.getDataSaida()
        ));

        return salvo;
    }

    @Transactional
    public ResultadoPagamento processarPagamento(Long aluguelId, String nomeMeio, DadosPagamento dados) {
        Aluguel aluguel = buscarPorId(aluguelId);

        ResultadoPagamento resultado = processadorPagamento.processar(
                nomeMeio, aluguel.getValorFinal(), dados);

        if (resultado.isSucesso()) {
            centralNotificacoes.publicar(new ContextoNotificacao(
                    EventoHospedagem.PAGAMENTO_CONFIRMADO,
                    aluguelId,
                    aluguel.getCliente().getNome(),
                    aluguel.getCliente().getEmail(),
                    aluguel.getCliente().getTelefone(),
                    String.format("%.2f", resultado.getValorProcessado())
            ));
        }

        return resultado;
    }

    @Transactional
    public void cancelarAluguel(Long id) {
        Aluguel aluguel = buscarPorId(id);
        aluguel.setCancelado(true);
        aluguelRepository.save(aluguel);

        GerenciadorLogs.getInstance().warn(
                "Aluguel #" + id + " cancelado por solicitação.");

        centralNotificacoes.publicar(new ContextoNotificacao(
                EventoHospedagem.RESERVA_CANCELADA,
                id,
                aluguel.getCliente().getNome(),
                aluguel.getCliente().getEmail(),
                aluguel.getCliente().getTelefone(),
                "Reserva cancelada em " + java.time.LocalDateTime.now()
        ));
    }

    public String emitirFormulario(Long aluguelId) {
        Aluguel aluguel = buscarPorId(aluguelId);
        return aluguel.emitirFormulario();
    }
}
