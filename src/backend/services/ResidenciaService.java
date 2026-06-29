package com.hospedagem.service;

import com.hospedagem.exception.EntidadeNaoEncontradaException;
import com.hospedagem.model.Residencia;
import com.hospedagem.repository.ResidenciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResidenciaService {

    private final ResidenciaRepository repository;

    public ResidenciaService(ResidenciaRepository repository) {
        this.repository = repository;
    }

    public List<Residencia> listarTodas() {
        return repository.findAll();
    }

    public Residencia buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Residência não encontrada: " + id));
    }

    public Residencia salvar(Residencia residencia) {
        return repository.save(residencia);
    }

    public Residencia atualizar(Long id, Residencia dados) {
        Residencia existente = buscarPorId(id);
        existente.setEndereco(dados.getEndereco());
        existente.setNumero(dados.getNumero());
        existente.setBairro(dados.getBairro());
        existente.setCep(dados.getCep());
        existente.setTelefone(dados.getTelefone());
        existente.setEmail(dados.getEmail());
        return repository.save(existente);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }
}
