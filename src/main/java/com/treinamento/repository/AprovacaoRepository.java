package com.treinamento.repository;

import com.treinamento.model.Aprovacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AprovacaoRepository extends JpaRepository<Aprovacao, Long> {
    List<Aprovacao> findByStatus(String status);
    List<Aprovacao> findByMatriculaAndStatus(String matricula, String status);
}
