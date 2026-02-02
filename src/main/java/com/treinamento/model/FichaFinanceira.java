package com.treinamento.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "SRD")
public class FichaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4)
    private String filial;

    @Column(length = 6)
    private String matricula; // Parte da Chave

    @Column(length = 3)
    private String verba; // Parte da Chave

    @Column(length = 1)
    private String tipo;

    @Column(precision = 12, scale = 2)
    private BigDecimal horas;

    @Column(precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(length = 6)
    private String periodo; // Parte da Chave (AAAAMM)

    @Column(length = 3)
    private String roteiro; // Parte da Chave

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getVerba() { return verba; }
    public void setVerba(String verba) { this.verba = verba; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public BigDecimal getHoras() { return horas; }
    public void setHoras(BigDecimal horas) { this.horas = horas; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    public String getRoteiro() { return roteiro; }
    public void setRoteiro(String roteiro) { this.roteiro = roteiro; }
}