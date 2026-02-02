package com.treinamento.repository;

import com.treinamento.model.Integracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegracaoRepository extends JpaRepository<Integracao, Long> {
}
