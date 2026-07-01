package com.hospedagem.repository;

import com.hospedagem.model.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AluguelRepository extends JpaRepository<Aluguel, Long> {
    List<Aluguel> findByResidenciaId(Long residenciaId);
    List<Aluguel> findByClienteId(Long clienteId);
    List<Aluguel> findByQuartoId(Long quartoId);
}
