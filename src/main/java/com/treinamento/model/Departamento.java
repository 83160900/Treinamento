package com.treinamento.model;

import jakarta.persistence.*;

@Entity
@Table(name = "departamentos")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3, nullable = false)
    private String filial;

    @Column(length = 3, nullable = false)
    private String codigo;

    @Column(length = 12, nullable = false)
    private String nome;

    public Departamento() {}

    public Departamento(String filial, String codigo, String nome) {
        this.filial = filial;
        this.codigo = codigo;
        this.nome = nome;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
