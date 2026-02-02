package com.treinamento.repository;

import com.treinamento.model.Escala;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EscalaRepository extends JpaRepository<Escala, Long> {
    List<Escala> findByFilial(String filial);
}
