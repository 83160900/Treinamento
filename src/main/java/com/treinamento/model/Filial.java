package com.treinamento.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Filial")
public class Filial {

    @Id
    @Column(length = 3)
    private String codFilial;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(length = 40)
    private String logradouro;

    @Column(length = 6)
    private String num;

    @Column(length = 2)
    private String estado;

    @Column(length = 15)
    private String municipio;

    @Column(length = 14)
    private String cnpj;

    public Filial() {}

    public Filial(String codFilial, String nome, String logradouro, String num, String estado, String municipio, String cnpj) {
        this.codFilial = codFilial;
        this.nome = nome;
        this.logradouro = logradouro;
        this.num = num;
        this.estado = estado;
        this.municipio = municipio;
        this.cnpj = cnpj;
    }

    // Getters e Setters
    public String getCodFilial() { return codFilial; }
    public void setCodFilial(String codFilial) { this.codFilial = codFilial; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNum() { return num; }
    public void setNum(String num) { this.num = num; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
}
