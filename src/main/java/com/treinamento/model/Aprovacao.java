package com.treinamento.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "aprovacoes")
@Filter(name = "tenantFilter", condition = "filial = :tenantId")
public class Aprovacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filial;
    private String matricula;
    private String nomeFuncionario;
    private LocalDate dataMarcacao;
    
    private String tipo; // INCLUSAO, ALTERACAO, EXCLUSAO, ABONO
    
    // Novos valores propostos
    private String e1;
    private String s1;
    private String e2;
    private String s2;
    
    private Long idAbono;
    private String horasAbonadas;
    private boolean abonoDiaTodo;
    
    private String status; // PENDENTE, EM_EFETIVACAO, APROVADO, REJEITADO
    private String justificativa;
    private String anexoNome;
    
    private Double latitude;
    private Double longitude;
    
    private String solicitanteCpf;
    private String aprovadorCpf;
    private String efetivadorCpf;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataAprovacao;
    private LocalDateTime dataEfetivacao;
    
    @Column(columnDefinition = "TEXT")
    private String historico;

    public Aprovacao() {
        this.status = "PENDENTE";
        this.dataSolicitacao = LocalDateTime.now();
        this.historico = "";
    }

    public void addHistorico(String mensagem) {
        String data = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        this.historico += "[" + data + "] " + mensagem + "\n";
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String nomeFuncionario) { this.nomeFuncionario = nomeFuncionario; }
    public LocalDate getDataMarcacao() { return dataMarcacao; }
    public void setDataMarcacao(LocalDate dataMarcacao) { this.dataMarcacao = dataMarcacao; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getE1() { return e1; }
    public void setE1(String e1) { this.e1 = e1; }
    public String getS1() { return s1; }
    public void setS1(String s1) { this.s1 = s1; }
    public String getE2() { return e2; }
    public void setE2(String e2) { this.e2 = e2; }
    public String getS2() { return s2; }
    public void setS2(String s2) { this.s2 = s2; }
    public Long getIdAbono() { return idAbono; }
    public void setIdAbono(Long idAbono) { this.idAbono = idAbono; }
    public String getHorasAbonadas() { return horasAbonadas; }
    public void setHorasAbonadas(String horasAbonadas) { this.horasAbonadas = horasAbonadas; }
    public boolean isAbonoDiaTodo() { return abonoDiaTodo; }
    public void setAbonoDiaTodo(boolean abonoDiaTodo) { this.abonoDiaTodo = abonoDiaTodo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    public String getAnexoNome() { return anexoNome; }
    public void setAnexoNome(String anexoNome) { this.anexoNome = anexoNome; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getSolicitanteCpf() { return solicitanteCpf; }
    public void setSolicitanteCpf(String solicitanteCpf) { this.solicitanteCpf = solicitanteCpf; }
    public String getAprovadorCpf() { return aprovadorCpf; }
    public void setAprovadorCpf(String aprovadorCpf) { this.aprovadorCpf = aprovadorCpf; }
    public String getEfetivadorCpf() { return efetivadorCpf; }
    public void setEfetivadorCpf(String efetivadorCpf) { this.efetivadorCpf = efetivadorCpf; }
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
    public LocalDateTime getDataAprovacao() { return dataAprovacao; }
    public void setDataAprovacao(LocalDateTime dataAprovacao) { this.dataAprovacao = dataAprovacao; }
    public LocalDateTime getDataEfetivacao() { return dataEfetivacao; }
    public void setDataEfetivacao(LocalDateTime dataEfetivacao) { this.dataEfetivacao = dataEfetivacao; }
    public String getHistorico() { return historico; }
    public void setHistorico(String historico) { this.historico = historico; }
}
