package com.hospedagem.controller;

import com.hospedagem.model.Aluguel;
import com.hospedagem.model.Residencia;
import com.hospedagem.service.AluguelService;
import com.hospedagem.service.ResidenciaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/residencias")
public class ResidenciaController {

    private final ResidenciaService service;
    private final AluguelService aluguelService;

    public ResidenciaController(ResidenciaService service, AluguelService aluguelService) {
        this.service = service;
        this.aluguelService = aluguelService;
    }

    @GetMapping
    public List<Residencia> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Residencia> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Residencia> criar(@RequestBody Residencia residencia) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(residencia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Residencia> atualizar(@PathVariable Long id, @RequestBody Residencia dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/historico")
    public List<Aluguel> historico(@PathVariable Long id) {
        return aluguelService.historicoPorResidencia(id);
    }
}
