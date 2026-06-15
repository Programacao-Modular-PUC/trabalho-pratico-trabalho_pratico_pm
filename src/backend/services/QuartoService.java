package com.hospedagem.service;

import com.hospedagem.exception.EntidadeNaoEncontradaException;
import com.hospedagem.model.*;
import com.hospedagem.repository.QuartoRepository;
import com.hospedagem.repository.ResidenciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final ResidenciaRepository residenciaRepository;

    public QuartoService(QuartoRepository quartoRepository, ResidenciaRepository residenciaRepository) {
        this.quartoRepository = quartoRepository;
        this.residenciaRepository = residenciaRepository;
    }

    public List<Quarto> listarTodos() {
        return quartoRepository.findAll();
    }

    public List<Quarto> listarPorResidencia(Long residenciaId) {
        return quartoRepository.findByResidenciaId(residenciaId);
    }

    public List<Quarto> listarPorTipo(String tipo) {
        Class<? extends Quarto> classeTipo = resolverTipo(tipo);
        return quartoRepository.findByTipo(classeTipo);
    }

    public Quarto buscarPorId(Long id) {
        return quartoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Quarto não encontrado: " + id));
    }

    public Quarto salvar(Long residenciaId, Quarto quarto) {
        Residencia residencia = residenciaRepository.findById(residenciaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Residência não encontrada: " + residenciaId));
        quarto.setResidencia(residencia);
        return quartoRepository.save(quarto);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        quartoRepository.deleteById(id);
    }

    public boolean verificarDisponibilidade(Long quartoId, LocalDateTime entrada, LocalDateTime saida) {
        buscarPorId(quartoId);
        return quartoRepository.isDisponivel(quartoId, entrada, saida);
    }

    private Class<? extends Quarto> resolverTipo(String tipo) {
        return switch (tipo.trim().toUpperCase()) {
            case "INDIVIDUAL" -> QuartoIndividual.class;
            case "DUPLO" -> QuartoDuplo.class;
            case "FAMILIA" -> QuartoFamilia.class;
            default -> throw new IllegalArgumentException("Tipo de quarto inválido: " + tipo);
        };
    }
}
