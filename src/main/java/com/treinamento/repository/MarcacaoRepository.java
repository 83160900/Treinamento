package com.treinamento.repository;

import com.treinamento.model.Marcacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MarcacaoRepository extends JpaRepository<Marcacao, Long> {
    List<Marcacao> findByMatricula(String matricula);
    List<Marcacao> findByDataBetween(LocalDate inicio, LocalDate fim);
    java.util.Optional<Marcacao> findByMatriculaAndData(String matricula, LocalDate data);
    List<Marcacao> findByMatriculaAndDataBetween(String matricula, LocalDate inicio, LocalDate fim);
    List<Marcacao> findByFilialAndMatriculaAndDataBetween(String filial, String matricula, LocalDate inicio, LocalDate fim);
}
