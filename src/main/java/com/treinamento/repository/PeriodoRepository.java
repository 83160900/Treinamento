package com.treinamento.repository;

import com.treinamento.model.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, Long> {
    Optional<Periodo> findByAtivoTrue();
    Optional<Periodo> findByFilialAndAtivoTrue(String filial);
    List<Periodo> findByFilial(String filial);
}
