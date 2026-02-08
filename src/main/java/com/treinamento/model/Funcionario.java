package com.treinamento.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "SRA") // Tabela de funcionários no Protheus
@IdClass(FuncionarioId.class)
public class Funcionario {
    @Id
    @Column(length = 3)
    private String filial;

    @Id
    @Column(length = 6)
    private String matricula;

    private String nome;

    @Column(unique = true, length = 11)
    private String cpf;

    private String senha;
    
    private String perfil; // ADMIN, GESTOR, COLABORADOR
    
    private String departamento;
    
    private boolean trocarSenha = true;

    private Long idHorario;
    private Long idEscala;

    // Getters e Setters
    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public boolean isTrocarSenha() { return trocarSenha; }
    public void setTrocarSenha(boolean trocarSenha) { this.trocarSenha = trocarSenha; }

    public Long getIdHorario() { return idHorario; }
    public void setIdHorario(Long idHorario) { this.idHorario = idHorario; }

    public Long getIdEscala() { return idEscala; }
    public void setIdEscala(Long idEscala) { this.idEscala = idEscala; }
}
