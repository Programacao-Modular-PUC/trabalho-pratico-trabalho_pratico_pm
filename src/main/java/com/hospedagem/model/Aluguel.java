package com.hospedagem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospedagem.exception.DataInvalidaException;
import com.hospedagem.exception.RecursoNaoPermitidoException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Entity
@Table(name = "alugueis")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residencia_id", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler",
            "quartos",
            "historicoAlugueis"
    })
    private Residencia residencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quarto_id", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler",
            "residencia"
    })
    private Quarto quarto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
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

    @Column(name = "cancelado")
    private boolean cancelado = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pagamento_id")
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler",
            "aluguel"
    })
    private Pagamento pagamento;

    public void validarDatas() {
        if (dataEntrada == null || dataSaida == null) {
            throw new DataInvalidaException("As datas de entrada e saida sao obrigatorias.");
        }

        if (!dataSaida.isAfter(dataEntrada)) {
            throw new DataInvalidaException("A data de saida deve ser posterior a data de entrada.");
        }
    }

    public void validarBerco() {
        if (solicitouBerco && (quarto == null || !quarto.permiteBerco())) {
            throw new RecursoNaoPermitidoException("Berco nao e permitido para o tipo de quarto selecionado.");
        }
    }

    public int calcularDiarias() {
        LocalDateTime checkIn = dataEntrada;
        LocalDateTime checkOut = dataSaida;

        long dias = ChronoUnit.DAYS.between(
                checkIn.toLocalDate(),
                checkOut.toLocalDate()
        );

        if (checkOut.getHour() > 12 || (checkOut.getHour() == 12 && checkOut.getMinute() > 0)) {
            dias += 1;
        }

        return (int) Math.max(1, dias);
    }

    public double calcularValorFinal() {
        validarDatas();
        validarBerco();

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

        Pagamento pagamentoGerado = new Pagamento();
        pagamentoGerado.setValorTotal(this.valorFinal);
        pagamentoGerado.setNumDiarias(this.qtdDiarias);
        pagamentoGerado.setDataPagamento(LocalDateTime.now());

        this.pagamento = pagamentoGerado;
        return pagamentoGerado;
    }

    public String emitirFormulario() {
        return String.format("""
                ===== FORMULARIO DE ALUGUEL =====
                Data e horario de entrada: %s
                Data e horario de saida:   %s
                Numero de diarias:         %d
                Total a pagar:             R$ %.2f
                =================================
                """,
                dataEntrada,
                dataSaida,
                qtdDiarias,
                valorFinal
        );
    }

    public boolean isReservaFutura() {
        return dataEntrada != null && dataEntrada.isAfter(LocalDateTime.now());
    }
}
