package com.hospedagem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_total", nullable = false)
    private double valorTotal;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDateTime dataPagamento;

    @Column(name = "num_diarias", nullable = false)
    private int numDiarias;

   @JsonIgnore
@OneToOne(mappedBy = "pagamento")
private Aluguel aluguel;

    public void emitirRecibo() {
        System.out.println("===== RECIBO DE PAGAMENTO =====");
        System.out.println("Valor total: R$ " + String.format("%.2f", valorTotal));
        System.out.println("Número de diárias: " + numDiarias);
        System.out.println("Data do pagamento: " + dataPagamento);
        System.out.println("================================");
    }
}
