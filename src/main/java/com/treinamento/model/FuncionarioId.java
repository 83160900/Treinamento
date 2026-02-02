package com.treinamento.model;

import java.io.Serializable;
import java.util.Objects;

public class FuncionarioId implements Serializable {
    private String filial;
    private String matricula;

    public FuncionarioId() {}

    public FuncionarioId(String filial, String matricula) {
        this.filial = filial;
        this.matricula = matricula;
    }

    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuncionarioId that = (FuncionarioId) o;
        return Objects.equals(filial, that.filial) && Objects.equals(matricula, that.matricula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filial, matricula);
    }
}
