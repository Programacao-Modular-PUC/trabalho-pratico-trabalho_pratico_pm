package com.hospedagem.observer;

public interface CanalNotificacao {

    String getNome();

    void notificar(ContextoNotificacao contexto);
}
