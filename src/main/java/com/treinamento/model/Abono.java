package com.treinamento.model;

import jakarta.persistence.*;

@Entity
@Table(name = "abonos")
public class Abono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3, nullable = false)
    private String filial;

    @Column(length = 3, nullable = false)
    private String codigo;

    @Column(length = 15, nullable = false)
    private String descricao;

    @Column(length = 3)
    private String abonaDsr; // "SIM" ou "NAO"

    @Column(length = 3)
    private String anexo; // "SIM" ou "NAO"

    public Abono() {}

    public Abono(String filial, String codigo, String descricao, String abonaDsr, String anexo) {
        this.filial = filial;
        this.codigo = codigo;
        this.descricao = descricao;
        this.abonaDsr = abonaDsr;
        this.anexo = anexo;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getAbonaDsr() { return abonaDsr; }
    public void setAbonaDsr(String abonaDsr) { this.abonaDsr = abonaDsr; }

    public String getAnexo() { return anexo; }
    public void setAnexo(String anexo) { this.anexo = anexo; }
}
