package com.treinamento.model;

import jakarta.persistence.*;

@Entity
@Table(name = "escalas")
public class Escala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3)
    private String filial;

    @Column(length = 3)
    private String codigo;

    private Long idHorario;

    @Column(length = 15)
    private String segunda;

    @Column(length = 15)
    private String terca;

    @Column(length = 15)
    private String quarta;

    @Column(length = 15)
    private String quinta;

    @Column(length = 15)
    private String sexta;

    @Column(length = 15)
    private String sabado;

    @Column(length = 15)
    private String domingo;

    @Column(length = 10)
    private String segExtra;

    @Column(length = 10)
    private String terExtra;

    @Column(length = 10)
    private String quaExtra;

    @Column(length = 10)
    private String quiExtra;

    @Column(length = 10)
    private String sexExtra;

    @Column(length = 10)
    private String sabExtra;

    @Column(length = 10)
    private String domExtra;

    public Escala() {}

    public Escala(String filial, String codigo, Long idHorario, String segunda, String terca, String quarta, String quinta, String sexta, String sabado, String domingo) {
        this.filial = filial;
        this.codigo = codigo;
        this.idHorario = idHorario;
        this.segunda = segunda;
        this.terca = terca;
        this.quarta = quarta;
        this.quinta = quinta;
        this.sexta = sexta;
        this.sabado = sabado;
        this.domingo = domingo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public Long getIdHorario() { return idHorario; }
    public void setIdHorario(Long idHorario) { this.idHorario = idHorario; }
    public String getSegunda() { return segunda; }
    public void setSegunda(String segunda) { this.segunda = segunda; }
    public String getTerca() { return terca; }
    public void setTerca(String terca) { this.terca = terca; }
    public String getQuarta() { return quarta; }
    public void setQuarta(String quarta) { this.quarta = quarta; }
    public String getQuinta() { return quinta; }
    public void setQuinta(String quinta) { this.quinta = quinta; }
    public String getSexta() { return sexta; }
    public void setSexta(String sexta) { this.sexta = sexta; }
    public String getSabado() { return sabado; }
    public void setSabado(String sabado) { this.sabado = sabado; }
    public String getDomingo() { return domingo; }
    public void setDomingo(String domingo) { this.domingo = domingo; }

    public String getSegExtra() { return segExtra; }
    public void setSegExtra(String segExtra) { this.segExtra = segExtra; }
    public String getTerExtra() { return terExtra; }
    public void setTerExtra(String terExtra) { this.terExtra = terExtra; }
    public String getQuaExtra() { return quaExtra; }
    public void setQuaExtra(String quaExtra) { this.quaExtra = quaExtra; }
    public String getQuiExtra() { return quiExtra; }
    public void setQuiExtra(String quiExtra) { this.quiExtra = quiExtra; }
    public String getSexExtra() { return sexExtra; }
    public void setSexExtra(String sexExtra) { this.sexExtra = sexExtra; }
    public String getSabExtra() { return sabExtra; }
    public void setSabExtra(String sabExtra) { this.sabExtra = sabExtra; }
    public String getDomExtra() { return domExtra; }
    public void setDomExtra(String domExtra) { this.domExtra = domExtra; }
}
