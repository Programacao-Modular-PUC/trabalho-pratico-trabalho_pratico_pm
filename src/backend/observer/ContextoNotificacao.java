package com.hospedagem.observer;

public class ContextoNotificacao {

    private final EventoHospedagem evento;
    private final Long aluguelId;
    private final String nomeCliente;
    private final String emailCliente;
    private final String telefoneCliente;
    private final String detalhes;

    public ContextoNotificacao(EventoHospedagem evento, Long aluguelId,
                                String nomeCliente, String emailCliente,
                                String telefoneCliente, String detalhes) {
        this.evento = evento;
        this.aluguelId = aluguelId;
        this.nomeCliente = nomeCliente;
        this.emailCliente = emailCliente;
        this.telefoneCliente = telefoneCliente;
        this.detalhes = detalhes;
    }

    public EventoHospedagem getEvento() { return evento; }
    public Long getAluguelId() { return aluguelId; }
    public String getNomeCliente() { return nomeCliente; }
    public String getEmailCliente() { return emailCliente; }
    public String getTelefoneCliente() { return telefoneCliente; }
    public String getDetalhes() { return detalhes; }
}
