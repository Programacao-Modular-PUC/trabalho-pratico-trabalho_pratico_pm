package com.hospedagem.observer;

import com.hospedagem.singleton.GerenciadorLogs;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CentralNotificacoes {

    private final List<CanalNotificacao> canais = new ArrayList<>();

    public CentralNotificacoes() {
        registrar(new CanalEmail());
        registrar(new CanalSms());
        registrar(new CanalNotificacaoInterna());
    }

    public void registrar(CanalNotificacao canal) {
        canais.add(canal);
        GerenciadorLogs.getInstance().info("Canal registrado na central: " + canal.getNome());
    }

    public void remover(CanalNotificacao canal) {
        canais.remove(canal);
    }

    public void publicar(ContextoNotificacao contexto) {
        GerenciadorLogs.getInstance().info(
                "Publicando evento " + contexto.getEvento().name()
                + " para " + canais.size() + " canal(is).");
        for (CanalNotificacao canal : canais) {
            try {
                canal.notificar(contexto);
            } catch (Exception e) {
                GerenciadorLogs.getInstance().erro(
                        "Falha ao notificar via " + canal.getNome() + ": " + e.getMessage());
            }
        }
    }
}
