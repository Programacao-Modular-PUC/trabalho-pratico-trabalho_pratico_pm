package com.hospedagem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Entity
@Table(name = "alugueis")
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residencia_id", nullable = false)
    private Residencia residencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quarto_id", nullable = false)
    private Quarto quarto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida", nullable = false)
    private LocalDateTime dataSaida;

    @Column(name = "qtd_diarias")
    private int qtdDiarias;

    @Column(name = "valor_final")
    private double valorFinal;

    @Column(name = "solicitou_berco")
    private boolean solicitouBerco = false;

    @Column(name = "num_hospedes")
    private int numHospedes = 1;

    @Column(name = "reserva_futura")
    private boolean reservaFutura = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pagamento_id")
    private Pagamento pagamento;

    public int calcularDiarias() {
        LocalDateTime checkIn = dataEntrada;
        LocalDateTime checkOut = dataSaida;

        long dias = ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());

        if (checkOut.getHour() > 12 || (checkOut.getHour() == 12 && checkOut.getMinute() > 0)) {
            dias += 1;
        }

        return (int) Math.max(1, dias);
    }

    public double calcularValorFinal() {
        this.qtdDiarias = calcularDiarias();

        double valorDiaria;
        if (quarto instanceof QuartoDuplo quartoDuplo) {
            valorDiaria = quartoDuplo.calcularDiariaComBerco(solicitouBerco);
        } else if (quarto instanceof QuartoFamilia quartoFamilia) {
            valorDiaria = quartoFamilia.calcularDiariaPorHospedes(numHospedes);
        } else {
            valorDiaria = quarto.calcularDiaria();
        }

        this.valorFinal = valorDiaria * qtdDiarias;
        return this.valorFinal;
    }

    public Pagamento gerarPagamento() {
        calcularValorFinal();
        Pagamento p = new Pagamento();
        p.setValorTotal(this.valorFinal);
        p.setNumDiarias(this.qtdDiarias);
        p.setDataPagamento(LocalDateTime.now());
        this.pagamento = p;
        return p;
    }

    public String emitirFormulario() {
        return String.format("""
                ===== FORMULÁRIO DE ALUGUEL =====
                Data e horário de entrada: %s
                Data e horário de saída:   %s
                Número de diárias:         %d
                Total a pagar:             R$ %.2f
                =================================
                """,
                dataEntrada, dataSaida, qtdDiarias, valorFinal);
    }

    public boolean isReservaFutura() {
        return dataEntrada.isAfter(LocalDateTime.now());
    }
}
