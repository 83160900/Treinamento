package com.treinamento.repository;

import com.treinamento.model.Funcionario;
import com.treinamento.model.FuncionarioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, FuncionarioId> {
    Optional<Funcionario> findByCpf(String cpf);
    List<Funcionario> findByDepartamento(String departamento);
}