package com.hospedagem.strategy;

import com.hospedagem.exception.RecursoNaoPermitidoException;
import com.hospedagem.singleton.GerenciadorLogs;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ProcessadorPagamento {

    private final Map<String, MeioPagamento> estrategias = new HashMap<>();

    public ProcessadorPagamento() {
        registrar(new PagamentoPix());
        registrar(new PagamentoCartaoCredito());
        registrar(new PagamentoCartaoDebito());
        registrar(new PagamentoDinheiro());
        registrar(new PagamentoCarteiraDigital());
    }

    public void registrar(MeioPagamento meio) {
        estrategias.put(meio.getNome().toUpperCase(), meio);
    }

    public Set<String> meiosDisponiveis() {
        return estrategias.keySet();
    }

    public ResultadoPagamento processar(String nomeMeio, double valor, DadosPagamento dados) {
        MeioPagamento meio = estrategias.get(nomeMeio.toUpperCase());

        if (meio == null) {
            throw new IllegalArgumentException("Meio de pagamento não suportado: " + nomeMeio);
        }

        if (!meio.validar(dados)) {
            GerenciadorLogs.getInstance().warn(
                    "Dados inválidos para pagamento via " + nomeMeio);
            throw new RecursoNaoPermitidoException(
                    "Dados inválidos para pagamento via " + meio.getNome());
        }

        ResultadoPagamento resultado = meio.processar(valor);

        if (resultado.isSucesso()) {
            GerenciadorLogs.getInstance().info(
                    "Pagamento processado via " + nomeMeio
                    + " | Código: " + resultado.getCodigoTransacao()
                    + " | Valor: R$ " + String.format("%.2f", resultado.getValorProcessado()));
        } else {
            GerenciadorLogs.getInstance().erro(
                    "Falha no pagamento via " + nomeMeio + ": " + resultado.getMensagem());
        }

        return resultado;
    }
}
