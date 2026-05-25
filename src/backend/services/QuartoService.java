package com.hospedagem.service;

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

    public Quarto buscarPorId(Long id) {
        return quartoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado: " + id));
    }

    public Quarto salvar(Long residenciaId, Quarto quarto) {
        Residencia residencia = residenciaRepository.findById(residenciaId)
                .orElseThrow(() -> new RuntimeException("Residência não encontrada: " + residenciaId));
        quarto.setResidencia(residencia);
        return quartoRepository.save(quarto);
    }

    public void deletar(Long id) {
        quartoRepository.deleteById(id);
    }

    public boolean verificarDisponibilidade(Long quartoId, LocalDateTime entrada, LocalDateTime saida) {
        return quartoRepository.isDisponivel(quartoId, entrada, saida);
    }
}
