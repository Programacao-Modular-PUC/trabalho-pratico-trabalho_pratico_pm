package com.hospedagem.repository;

import com.hospedagem.model.Quarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    List<Quarto> findByResidenciaId(Long residenciaId);

    @Query("SELECT q FROM Quarto q WHERE TYPE(q) = :tipo")
    List<Quarto> findByTipo(@Param("tipo") Class<? extends Quarto> tipo);

    @Query("""
            SELECT CASE WHEN COUNT(a) = 0 THEN true ELSE false END
            FROM Aluguel a
            WHERE a.quarto.id = :quartoId
              AND a.dataEntrada < :saida
              AND a.dataSaida > :entrada
            """)
    boolean isDisponivel(@Param("quartoId") Long quartoId,
                          @Param("entrada") LocalDateTime entrada,
                          @Param("saida") LocalDateTime saida);
}
