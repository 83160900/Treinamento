package com.treinamento.repository;

import com.treinamento.model.Feriado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeriadoRepository extends JpaRepository<Feriado, Long> {
    List<Feriado> findByAnoOrderByData(Integer ano);
    List<Feriado> findByTipo(String tipo);
    void deleteByTipo(String tipo);
}
