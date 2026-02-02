package com.treinamento.repository;

import com.treinamento.model.FichaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FichaFinanceiraRepository extends JpaRepository<FichaFinanceira, Long> {

    // Busca registros baseada na chave: Matricula, Periodo, Roteiro e Verba
    List<FichaFinanceira> findByMatriculaAndPeriodoAndRoteiroAndVerba(
            String matricula,
            String periodo,
            String roteiro,
            String verba
    );

    List<FichaFinanceira> findByMatricula(String matricula);
}