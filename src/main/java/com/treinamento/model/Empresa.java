package com.treinamento.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "Empresa")
@Filter(name = "tenantFilter", condition = "cod_empresa = :tenantId")
public class Empresa {

    @Id
    @Column(name = "cod_empresa", length = 3)
    private String codEmpresa;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(length = 14)
    private String cnpj;

    public Empresa() {}

    public Empresa(String codEmpresa, String nome, String cnpj) {
        this.codEmpresa = codEmpresa;
        this.nome = nome;
        this.cnpj = cnpj;
    }

    public String getCodEmpresa() { return codEmpresa; }
    public void setCodEmpresa(String codEmpresa) { this.codEmpresa = codEmpresa; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
}
