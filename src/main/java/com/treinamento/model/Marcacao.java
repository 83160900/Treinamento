package com.treinamento.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "marcacoes")
public class Marcacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3)
    private String filial;

    @Column(length = 6, nullable = false)
    private String matricula;

    private LocalDate data;

    @Column(length = 5)
    private String e1;

    @Column(length = 5)
    private String s1;

    @Column(length = 5)
    private String e2;

    @Column(length = 5)
    private String s2;

    @Column(length = 1)
    private String origem; // I para integração, M para manual

    private Long idAbono;

    @Column(length = 5)
    private String horasAbonadas;

    private boolean abonoDiaTodo;

    @Column(columnDefinition = "TEXT")
    private String justificativa;

    private String anexoNome;

    // Construtores
    public Marcacao() {}

    public Marcacao(String filial, String matricula, LocalDate data, String e1, String s1, String e2, String s2, String origem) {
        this.filial = filial;
        this.matricula = matricula;
        this.data = data;
        this.e1 = e1;
        this.s1 = s1;
        this.e2 = e2;
        this.s2 = s2;
        this.origem = origem;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getE1() { return e1; }
    public void setE1(String e1) { this.e1 = e1; }

    public String getS1() { return s1; }
    public void setS1(String s1) { this.s1 = s1; }

    public String getE2() { return e2; }
    public void setE2(String e2) { this.e2 = e2; }

    public String getS2() { return s2; }
    public void setS2(String s2) { this.s2 = s2; }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }

    public Long getIdAbono() { return idAbono; }
    public void setIdAbono(Long idAbono) { this.idAbono = idAbono; }

    public String getHorasAbonadas() { return horasAbonadas; }
    public void setHorasAbonadas(String horasAbonadas) { this.horasAbonadas = horasAbonadas; }

    public boolean isAbonoDiaTodo() { return abonoDiaTodo; }
    public void setAbonoDiaTodo(boolean abonoDiaTodo) { this.abonoDiaTodo = abonoDiaTodo; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public String getAnexoNome() { return anexoNome; }
    public void setAnexoNome(String anexoNome) { this.anexoNome = anexoNome; }
}
