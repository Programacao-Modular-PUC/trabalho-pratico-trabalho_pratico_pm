package com.hospedagem.controller;

import com.hospedagem.model.Aluguel;
import com.hospedagem.service.AluguelService;
import com.hospedagem.strategy.DadosPagamento;
import com.hospedagem.strategy.ResultadoPagamento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/alugueis")
public class AluguelController {

    private final AluguelService service;

    public AluguelController(AluguelService service) {
        this.service = service;
    }

    @GetMapping
    public List<Aluguel> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluguel> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

  @PostMapping
public ResponseEntity<Aluguel> realizar(@RequestBody Map<String, Object> dados) {

    Aluguel aluguel = new Aluguel();

    Long clienteId = Long.valueOf(dados.get("clienteId").toString());
    Long residenciaId = Long.valueOf(dados.get("residenciaId").toString());
    Long quartoId = Long.valueOf(dados.get("quartoId").toString());

    aluguel.setCliente(service.buscarCliente(clienteId));
    aluguel.setResidencia(service.buscarResidencia(residenciaId));
    aluguel.setQuarto(service.buscarQuarto(quartoId));

    aluguel.setDataEntrada(
            LocalDateTime.parse(dados.get("dataEntrada").toString()));

    aluguel.setDataSaida(
            LocalDateTime.parse(dados.get("dataSaida").toString()));

    aluguel.setSolicitouBerco(
            Boolean.parseBoolean(
                    dados.getOrDefault("solicitouBerco", false).toString()));

    aluguel.setNumHospedes(
            Integer.parseInt(
                    dados.getOrDefault("numHospedes", 1).toString()));

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.realizarAluguel(aluguel));
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.cancelarAluguel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/formulario")
    public ResponseEntity<String> emitirFormulario(@PathVariable Long id) {
        return ResponseEntity.ok(service.emitirFormulario(id));
    }

    @PostMapping("/{id}/pagamento")
    public ResponseEntity<ResultadoPagamento> processarPagamento(
            @PathVariable Long id,
            @RequestParam String meio,
            @RequestBody DadosPagamento dados) {
        ResultadoPagamento resultado = service.processarPagamento(id, meio, dados);
        return resultado.isSucesso()
                ? ResponseEntity.ok(resultado)
                : ResponseEntity.badRequest().body(resultado);
    }
}
