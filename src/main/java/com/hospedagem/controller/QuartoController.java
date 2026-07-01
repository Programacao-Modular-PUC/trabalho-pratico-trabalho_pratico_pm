package com.hospedagem.controller;

import com.hospedagem.model.Quarto;
import com.hospedagem.model.QuartoDuplo;
import com.hospedagem.model.QuartoFamilia;
import com.hospedagem.model.QuartoIndividual;
import com.hospedagem.model.TipoCamaCasal;
import com.hospedagem.service.QuartoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/quartos")
public class QuartoController {

    private final QuartoService service;

    public QuartoController(QuartoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Quarto> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quarto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/residencia/{residenciaId}")
    public List<Quarto> listarPorResidencia(@PathVariable Long residenciaId) {
        return service.listarPorResidencia(residenciaId);
    }

    @GetMapping("/tipo/{tipo}")
    public List<Quarto> listarPorTipo(@PathVariable String tipo) {
        return service.listarPorTipo(tipo);
    }

    @PostMapping("/residencia/{residenciaId}")
public ResponseEntity<Quarto> criar(
        @PathVariable Long residenciaId,
        @RequestBody Map<String, Object> dados) {

    String tipo = (String) dados.get("tipo_quarto");

    Quarto quarto;

    if ("INDIVIDUAL".equalsIgnoreCase(tipo)) {
        QuartoIndividual q = new QuartoIndividual();
        q.setNumCamasSolteiro((Integer) dados.getOrDefault("numCamasSolteiro", 1));
        quarto = q;

    } else if ("DUPLO".equalsIgnoreCase(tipo)) {
        QuartoDuplo q = new QuartoDuplo();
        q.setTipoCama(TipoCamaCasal.CASAL);
        quarto = q;

    } else if ("FAMILIA".equalsIgnoreCase(tipo)) {
        QuartoFamilia q = new QuartoFamilia();
        q.setNumCamasSolteiro((Integer) dados.getOrDefault("numCamasSolteiro", 2));
        q.setNumCamasCasal((Integer) dados.getOrDefault("numCamasCasal", 1));
        q.setNumCamasEspeciais((Integer) dados.getOrDefault("numCamasEspeciais", 0));
        q.setAmbientes((String) dados.getOrDefault("ambientes", "Ambiente padrão"));
        quarto = q;

    } else {
        throw new IllegalArgumentException("Tipo de quarto inválido: " + tipo);
    }

    quarto.setValorBase(((Number) dados.get("valorBase")).doubleValue());
    quarto.setPossuiAR((Boolean) dados.getOrDefault("possuiAR", false));
    quarto.setPossuiHidro((Boolean) dados.getOrDefault("possuiHidro", false));

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.salvar(residenciaId, quarto));
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<Boolean> verificarDisponibilidade(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime entrada,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime saida) {
        return ResponseEntity.ok(service.verificarDisponibilidade(id, entrada, saida));
    }
}
