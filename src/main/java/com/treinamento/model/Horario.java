package com.treinamento.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "tabela_horario")
@Filter(name = "tenantFilter", condition = "filial = :tenantId")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3, nullable = false)
    private String filial;

    @Column(nullable = false)
    private String descricao;

    @Column(length = 5)
    private String e1;

    @Column(length = 5)
    private String s1;

    @Column(length = 5)
    private String e2;

    @Column(length = 5)
    private String s2;

    @Column(length = 10)
    private String tolerancia;

    @Column(length = 2)
    private String minutos;

    public Horario() {}

    public Horario(String filial, String descricao, String e1, String s1, String e2, String s2) {
        this.filial = filial;
        this.descricao = descricao;
        this.e1 = e1;
        this.s1 = s1;
        this.e2 = e2;
        this.s2 = s2;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getE1() { return e1; }
    public void setE1(String e1) { this.e1 = e1; }
    public String getS1() { return s1; }
    public void setS1(String s1) { this.s1 = s1; }
    public String getE2() { return e2; }
    public void setE2(String e2) { this.e2 = e2; }
    public String getS2() { return s2; }
    public void setS2(String s2) { this.s2 = s2; }
    public String getTolerancia() { return tolerancia; }
    public void setTolerancia(String tolerancia) { this.tolerancia = tolerancia; }
    public String getMinutos() { return minutos; }
    public void setMinutos(String minutos) { this.minutos = minutos; }
}
