package com.hospedagem.repository;

import com.hospedagem.model.Quarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    List<Quarto> findByResidenciaId(Long residenciaId);

    @Query("""
        SELECT q FROM Quarto q
        WHERE TYPE(q) = :tipo
    """)
    List<Quarto> findByTipo(@Param("tipo") Class<? extends Quarto> tipo);

    @Query("""
        SELECT COUNT(a) = 0 FROM Aluguel a
        WHERE a.quarto.id = :quartoId
        AND a.cancelado = false
        AND a.dataEntrada < :dataSaida
        AND a.dataSaida > :dataEntrada
    """)
    boolean isDisponivel(
            @Param("quartoId") Long quartoId,
            @Param("dataEntrada") LocalDateTime dataEntrada,
            @Param("dataSaida") LocalDateTime dataSaida);
}
