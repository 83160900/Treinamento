package com.treinamento.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Feriado")
public class Feriado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private LocalDate data;

    private String tipo; // N para Nacional, M para Municipal

    public Feriado() {}

    public Feriado(String descricao, LocalDate data, String tipo) {
        this.descricao = descricao;
        this.data = data;
        this.tipo = tipo;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
