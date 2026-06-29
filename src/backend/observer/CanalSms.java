package com.hospedagem.observer;

import com.hospedagem.singleton.GerenciadorLogs;

public class CanalSms implements CanalNotificacao {

    @Override
    public String getNome() {
        return "SMS";
    }

    @Override
    public void notificar(ContextoNotificacao contexto) {
        String texto = gerarTexto(contexto);
        String mensagem = String.format("[SMS] Para: %s | %s",
                contexto.getTelefoneCliente(), texto);

        GerenciadorLogs.getInstance().info(mensagem);
        System.out.println(mensagem);
    }

    private String gerarTexto(ContextoNotificacao contexto) {
        return switch (contexto.getEvento()) {
            case RESERVA_CRIADA -> "Olá " + contexto.getNomeCliente() + "! Reserva #"
                    + contexto.getAluguelId() + " confirmada.";
            case RESERVA_CANCELADA -> "Reserva #" + contexto.getAluguelId() + " cancelada.";
            case CHECKIN_REALIZADO -> "Check-in realizado. Boa estadia, " + contexto.getNomeCliente() + "!";
            case CHECKOUT_REALIZADO -> "Check-out concluído. Obrigado, " + contexto.getNomeCliente() + "!";
            case PAGAMENTO_CONFIRMADO -> "Pagamento de R$" + contexto.getDetalhes() + " confirmado.";
        };
    }
}
