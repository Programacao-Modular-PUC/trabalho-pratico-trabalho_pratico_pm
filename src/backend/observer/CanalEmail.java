package com.hospedagem.observer;

import com.hospedagem.singleton.GerenciadorLogs;

public class CanalEmail implements CanalNotificacao {

    @Override
    public String getNome() {
        return "EMAIL";
    }

    @Override
    public void notificar(ContextoNotificacao contexto) {
        String assunto = resolverAssunto(contexto.getEvento());
        String mensagem = String.format(
                "[EMAIL] Para: %s <%s> | Assunto: %s | %s",
                contexto.getNomeCliente(),
                contexto.getEmailCliente(),
                assunto,
                contexto.getDetalhes());

        GerenciadorLogs.getInstance().info(mensagem);
        System.out.println(mensagem);
    }

    private String resolverAssunto(EventoHospedagem evento) {
        return switch (evento) {
            case RESERVA_CRIADA -> "Reserva confirmada!";
            case RESERVA_CANCELADA -> "Sua reserva foi cancelada";
            case CHECKIN_REALIZADO -> "Check-in realizado com sucesso";
            case CHECKOUT_REALIZADO -> "Check-out realizado — obrigado!";
            case PAGAMENTO_CONFIRMADO -> "Pagamento confirmado";
        };
    }
}
