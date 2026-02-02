package com.treinamento.model;

import jakarta.persistence.*;


@Entity
@Table(name = "dependentes")
public class Dependente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    // Adicione outros campos, getters e setters conforme sua necessidade
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}