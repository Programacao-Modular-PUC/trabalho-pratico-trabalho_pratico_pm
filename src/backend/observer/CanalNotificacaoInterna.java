package com.hospedagem.observer;

import com.hospedagem.singleton.GerenciadorLogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CanalNotificacaoInterna implements CanalNotificacao {

    private final List<String> caixa = new ArrayList<>();

    @Override
    public String getNome() {
        return "INTERNA";
    }

    @Override
    public void notificar(ContextoNotificacao contexto) {
        String aviso = String.format("[INTERNA] Evento: %s | Aluguel #%d | Cliente: %s | %s",
                contexto.getEvento().name(),
                contexto.getAluguelId(),
                contexto.getNomeCliente(),
                contexto.getDetalhes());

        synchronized (caixa) {
            caixa.add(aviso);
        }

        GerenciadorLogs.getInstance().info(aviso);
        System.out.println(aviso);
    }

    public List<String> obterNotificacoes() {
        synchronized (caixa) {
            return Collections.unmodifiableList(new ArrayList<>(caixa));
        }
    }
}
