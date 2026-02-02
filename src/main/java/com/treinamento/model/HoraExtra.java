package com.treinamento.model;

import jakarta.persistence.*;

@Entity
@Table(name = "horas_extras")
public class HoraExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3, nullable = false)
    private String filial;

    @Column(length = 12, nullable = false)
    private String descricao;

    @Column(length = 4, nullable = false)
    private String percentual;

    public HoraExtra() {}

    public HoraExtra(String filial, String descricao, String percentual) {
        this.filial = filial;
        this.descricao = descricao;
        this.percentual = percentual;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getPercentual() { return percentual; }
    public void setPercentual(String percentual) { this.percentual = percentual; }
}
