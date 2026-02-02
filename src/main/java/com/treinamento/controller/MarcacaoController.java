package com.treinamento.controller;

import com.treinamento.model.Marcacao;
import com.treinamento.model.Periodo;
import com.treinamento.repository.MarcacaoRepository;
import com.treinamento.repository.PeriodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/marcacoes")
public class MarcacaoController {

    @Autowired
    private MarcacaoRepository marcacaoRepository;

    @Autowired
    private PeriodoRepository periodoRepository;

    @Autowired
    private com.treinamento.repository.FeriadoRepository feriadoRepository;

    @Autowired
    private com.treinamento.repository.FilialRepository filialRepository;

    @Autowired
    private com.treinamento.repository.AprovacaoRepository aprovacaoRepository;

    @Autowired
    private com.treinamento.repository.FuncionarioRepository funcionarioRepository;

    @GetMapping("/feriados")
    public List<com.treinamento.model.Feriado> listarFeriados() {
        return feriadoRepository.findAll();
    }

    @GetMapping
    public List<Marcacao> listar(@RequestParam(required = false) String filial, @RequestParam(required = false) String matricula) {
        Optional<Periodo> periodo = periodoRepository.findByFilialAndAtivoTrue(filial);
        if (periodo.isEmpty()) {
            periodo = periodoRepository.findByAtivoTrue();
        }
        
        if (matricula != null && !matricula.isEmpty()) {
            if (periodo.isPresent()) {
                if (filial != null && !filial.isEmpty()) {
                    return marcacaoRepository.findByFilialAndMatriculaAndDataBetween(
                        filial,
                        matricula, 
                        periodo.get().getDataInicio(), 
                        periodo.get().getDataFim()
                    );
                }
                return marcacaoRepository.findByMatriculaAndDataBetween(
                    matricula, 
                    periodo.get().getDataInicio(), 
                    periodo.get().getDataFim()
                );
            }
            return marcacaoRepository.findByMatricula(matricula);
        }

        if (periodo.isPresent()) {
            return marcacaoRepository.findByDataBetween(periodo.get().getDataInicio(), periodo.get().getDataFim());
        }
        
        return marcacaoRepository.findAll();
    }

    @GetMapping("/periodo")
    public ResponseEntity<Periodo> getPeriodoAtivo(@RequestParam(required = false) String filial) {
        Optional<Periodo> p = periodoRepository.findByFilialAndAtivoTrue(filial);
        if (p.isEmpty()) {
            p = periodoRepository.findByAtivoTrue();
        }
        return p.map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Marcacao novaMarcacao) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String cpfLogado = auth.getName();
        
        Optional<com.treinamento.model.Funcionario> logadoOpt = funcionarioRepository.findByCpf(cpfLogado);
        boolean isColaborador = logadoOpt.isPresent() && "COLABORADOR".equals(logadoOpt.get().getPerfil());

        // Validação da Filial
        if (novaMarcacao.getFilial() != null && !novaMarcacao.getFilial().isEmpty() && !filialRepository.existsById(novaMarcacao.getFilial())) {
            return ResponseEntity.status(400).body("Filial nao encontrada: " + novaMarcacao.getFilial());
        }

        // Se for Colaborador fazendo alteração manual, vai para aprovação
        if (isColaborador && "M".equals(novaMarcacao.getOrigem())) {
            com.treinamento.model.Aprovacao aprov = new com.treinamento.model.Aprovacao();
            aprov.setFilial(novaMarcacao.getFilial());
            aprov.setMatricula(novaMarcacao.getMatricula());
            aprov.setNomeFuncionario(logadoOpt.get().getNome());
            aprov.setDataMarcacao(novaMarcacao.getData());
            aprov.setE1(novaMarcacao.getE1());
            aprov.setS1(novaMarcacao.getS1());
            aprov.setE2(novaMarcacao.getE2());
            aprov.setS2(novaMarcacao.getS2());
            aprov.setSolicitanteCpf(cpfLogado);
            aprov.addHistorico("Solicitado por Colaborador: " + logadoOpt.get().getNome());
            
            Optional<Marcacao> existente = marcacaoRepository.findByMatriculaAndData(novaMarcacao.getMatricula(), novaMarcacao.getData());
            aprov.setTipo(existente.isPresent() ? "ALTERACAO" : "INCLUSAO");
            
            aprovacaoRepository.save(aprov);
            return ResponseEntity.ok().body(Map.of("status", "EM_APROVACAO", "message", "Sua solicitação foi enviada para aprovação do gestor."));
        }

        // Se for Gestor fazendo alteração manual, vai para aprovação do ADMIN (status EM_EFETIVACAO)
        boolean isGestor = logadoOpt.isPresent() && "GESTOR".equals(logadoOpt.get().getPerfil());
        if (isGestor && "M".equals(novaMarcacao.getOrigem())) {
            com.treinamento.model.Aprovacao aprov = new com.treinamento.model.Aprovacao();
            aprov.setFilial(novaMarcacao.getFilial());
            aprov.setMatricula(novaMarcacao.getMatricula());
            
            Optional<com.treinamento.model.Funcionario> funcAlvo = funcionarioRepository.findById(new com.treinamento.model.FuncionarioId(novaMarcacao.getFilial(), novaMarcacao.getMatricula()));
            if (funcAlvo.isPresent()) aprov.setNomeFuncionario(funcAlvo.get().getNome());
            
            aprov.setDataMarcacao(novaMarcacao.getData());
            aprov.setE1(novaMarcacao.getE1());
            aprov.setS1(novaMarcacao.getS1());
            aprov.setE2(novaMarcacao.getE2());
            aprov.setS2(novaMarcacao.getS2());
            aprov.setSolicitanteCpf(cpfLogado);
            aprov.setAprovadorCpf(cpfLogado);
            aprov.setDataAprovacao(java.time.LocalDateTime.now());
            aprov.setStatus("EM_EFETIVACAO");
            aprov.addHistorico("Solicitado e Aprovado por Gestor: " + logadoOpt.get().getNome() + ". Aguardando Efetivação do ADMIN.");
            
            Optional<Marcacao> existente = marcacaoRepository.findByMatriculaAndData(novaMarcacao.getMatricula(), novaMarcacao.getData());
            aprov.setTipo(existente.isPresent() ? "ALTERACAO" : "INCLUSAO");
            
            aprovacaoRepository.save(aprov);
            return ResponseEntity.ok().body(Map.of("status", "EM_EFETIVACAO", "message", "Sua solicitação foi enviada para efetivação do ADMIN."));
        }

        // Tenta encontrar uma marcação já existente para a mesma matrícula e data
        Optional<Marcacao> existenteOpt = marcacaoRepository.findByMatriculaAndData(
            novaMarcacao.getMatricula(), 
            novaMarcacao.getData()
        );

        if (existenteOpt.isPresent()) {
            Marcacao existente = existenteOpt.get();
            
            // Atualiza apenas os campos que foram enviados e não estão vazios
            if (novaMarcacao.getE1() != null && !novaMarcacao.getE1().isEmpty()) existente.setE1(novaMarcacao.getE1());
            if (novaMarcacao.getS1() != null && !novaMarcacao.getS1().isEmpty()) existente.setS1(novaMarcacao.getS1());
            if (novaMarcacao.getE2() != null && !novaMarcacao.getE2().isEmpty()) existente.setE2(novaMarcacao.getE2());
            if (novaMarcacao.getS2() != null && !novaMarcacao.getS2().isEmpty()) existente.setS2(novaMarcacao.getS2());
            
            // Se for manual, garante que a origem seja 'M'
            if ("M".equals(novaMarcacao.getOrigem())) {
                existente.setOrigem("M");
            }

            // Atualiza campos de abono se enviados
            if (novaMarcacao.getIdAbono() != null) existente.setIdAbono(novaMarcacao.getIdAbono());
            if (novaMarcacao.getHorasAbonadas() != null) existente.setHorasAbonadas(novaMarcacao.getHorasAbonadas());
            existente.setAbonoDiaTodo(novaMarcacao.isAbonoDiaTodo());

            return ResponseEntity.ok(marcacaoRepository.save(existente));
        }

        // Se não existir, salva a nova
        return ResponseEntity.ok(marcacaoRepository.save(novaMarcacao));
    }

    @PostMapping("/abonar")
    public ResponseEntity<?> abonar(
            @RequestParam("dados") String dadosJson,
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo) {
        
        Marcacao abonoDados;
        try {
            abonoDados = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()).readValue(dadosJson, Marcacao.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar dados do abono: " + e.getMessage());
        }

        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String cpfLogado = auth.getName();
        
        Optional<com.treinamento.model.Funcionario> logadoOpt = funcionarioRepository.findByCpf(cpfLogado);
        boolean isColaborador = logadoOpt.isPresent() && "COLABORADOR".equals(logadoOpt.get().getPerfil());

        // Validação básica
        if (abonoDados.getFilial() == null || abonoDados.getMatricula() == null || abonoDados.getData() == null) {
            return ResponseEntity.status(400).body("Filial, Matricula e Data sao obrigatorios");
        }

        String nomeSalvo = null;
        if (arquivo != null && !arquivo.isEmpty()) {
            try {
                String extensao = "";
                String originalFilename = arquivo.getOriginalFilename();
                if (originalFilename != null && originalFilename.contains(".")) {
                    extensao = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                nomeSalvo = UUID.randomUUID().toString() + extensao;
                Path path = Paths.get("uploads", nomeSalvo);
                Files.write(path, arquivo.getBytes());
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Erro ao salvar arquivo: " + e.getMessage());
            }
        }

        if (isColaborador) {
            com.treinamento.model.Aprovacao aprov = new com.treinamento.model.Aprovacao();
            aprov.setFilial(abonoDados.getFilial());
            aprov.setMatricula(abonoDados.getMatricula());
            aprov.setNomeFuncionario(logadoOpt.get().getNome());
            aprov.setDataMarcacao(abonoDados.getData());
            aprov.setTipo("ABONO");
            aprov.setIdAbono(abonoDados.getIdAbono());
            aprov.setHorasAbonadas(abonoDados.getHorasAbonadas());
            aprov.setAbonoDiaTodo(abonoDados.isAbonoDiaTodo());
            aprov.setJustificativa(abonoDados.getJustificativa());
            aprov.setAnexoNome(nomeSalvo);
            aprov.setSolicitanteCpf(cpfLogado);
            aprov.addHistorico("Abono Solicitado por Colaborador: " + logadoOpt.get().getNome());
            
            aprovacaoRepository.save(aprov);
            return ResponseEntity.ok().body(Map.of("status", "EM_APROVACAO", "message", "Seu abono foi enviado para aprovação do gestor."));
        }

        // Se for Gestor fazendo abono, vai para aprovação do ADMIN (status EM_EFETIVACAO)
        boolean isGestorAbono = logadoOpt.isPresent() && "GESTOR".equals(logadoOpt.get().getPerfil());
        if (isGestorAbono) {
            com.treinamento.model.Aprovacao aprov = new com.treinamento.model.Aprovacao();
            aprov.setFilial(abonoDados.getFilial());
            aprov.setMatricula(abonoDados.getMatricula());
            
            Optional<com.treinamento.model.Funcionario> funcAlvo = funcionarioRepository.findById(new com.treinamento.model.FuncionarioId(abonoDados.getFilial(), abonoDados.getMatricula()));
            if (funcAlvo.isPresent()) aprov.setNomeFuncionario(funcAlvo.get().getNome());
            
            aprov.setDataMarcacao(abonoDados.getData());
            aprov.setTipo("ABONO");
            aprov.setIdAbono(abonoDados.getIdAbono());
            aprov.setHorasAbonadas(abonoDados.getHorasAbonadas());
            aprov.setAbonoDiaTodo(abonoDados.isAbonoDiaTodo());
            aprov.setJustificativa(abonoDados.getJustificativa());
            aprov.setAnexoNome(nomeSalvo);
            aprov.setSolicitanteCpf(cpfLogado);
            aprov.setAprovadorCpf(cpfLogado);
            aprov.setDataAprovacao(java.time.LocalDateTime.now());
            aprov.setStatus("EM_EFETIVACAO");
            aprov.addHistorico("Abono Solicitado e Aprovado por Gestor: " + logadoOpt.get().getNome() + ". Aguardando Efetivação do ADMIN.");
            
            aprovacaoRepository.save(aprov);
            return ResponseEntity.ok().body(Map.of("status", "EM_EFETIVACAO", "message", "Seu abono foi enviado para efetivação do ADMIN."));
        }

        Optional<Marcacao> existenteOpt = marcacaoRepository.findByMatriculaAndData(
            abonoDados.getMatricula(), 
            abonoDados.getData()
        );

        Marcacao marcacao;
        if (existenteOpt.isPresent()) {
            marcacao = existenteOpt.get();
        } else {
            marcacao = new Marcacao();
            marcacao.setFilial(abonoDados.getFilial());
            marcacao.setMatricula(abonoDados.getMatricula());
            marcacao.setData(abonoDados.getData());
            marcacao.setOrigem("M");
        }

        marcacao.setIdAbono(abonoDados.getIdAbono());
        marcacao.setHorasAbonadas(abonoDados.getHorasAbonadas());
        marcacao.setAbonoDiaTodo(abonoDados.isAbonoDiaTodo());
        marcacao.setJustificativa(abonoDados.getJustificativa());
        if (nomeSalvo != null) {
            marcacao.setAnexoNome(nomeSalvo);
        }

        return ResponseEntity.ok(marcacaoRepository.save(marcacao));
    }

    @DeleteMapping("/matricula/{matricula}/data/{data}")
    public ResponseEntity<?> excluir(@PathVariable String matricula, @PathVariable String data) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String cpfLogado = auth.getName();
        
        Optional<com.treinamento.model.Funcionario> logadoOpt = funcionarioRepository.findByCpf(cpfLogado);
        boolean isColaborador = logadoOpt.isPresent() && "COLABORADOR".equals(logadoOpt.get().getPerfil());

        java.time.LocalDate localDate = java.time.LocalDate.parse(data);

        if (isColaborador) {
            com.treinamento.model.Aprovacao aprov = new com.treinamento.model.Aprovacao();
            aprov.setFilial(logadoOpt.get().getFilial());
            aprov.setMatricula(matricula);
            aprov.setNomeFuncionario(logadoOpt.get().getNome());
            aprov.setDataMarcacao(localDate);
            aprov.setTipo("EXCLUSAO");
            aprov.setSolicitanteCpf(cpfLogado);
            aprov.addHistorico("Exclusão Solicitada por Colaborador: " + logadoOpt.get().getNome());
            
            aprovacaoRepository.save(aprov);
            return ResponseEntity.ok().body(Map.of("status", "EM_APROVACAO", "message", "Sua solicitação de exclusão foi enviada para aprovação do gestor."));
        }

        // Se for Gestor fazendo exclusão, vai para aprovação do ADMIN (status EM_EFETIVACAO)
        boolean isGestorExcluir = logadoOpt.isPresent() && "GESTOR".equals(logadoOpt.get().getPerfil());
        if (isGestorExcluir) {
            com.treinamento.model.Aprovacao aprov = new com.treinamento.model.Aprovacao();
            aprov.setFilial(logadoOpt.get().getFilial());
            aprov.setMatricula(matricula);
            
            Optional<com.treinamento.model.Funcionario> funcAlvo = funcionarioRepository.findById(new com.treinamento.model.FuncionarioId(logadoOpt.get().getFilial(), matricula));
            if (funcAlvo.isPresent()) aprov.setNomeFuncionario(funcAlvo.get().getNome());
            
            aprov.setDataMarcacao(localDate);
            aprov.setTipo("EXCLUSAO");
            aprov.setSolicitanteCpf(cpfLogado);
            aprov.setAprovadorCpf(cpfLogado);
            aprov.setDataAprovacao(java.time.LocalDateTime.now());
            aprov.setStatus("EM_EFETIVACAO");
            aprov.addHistorico("Exclusão Solicitada e Aprovada por Gestor: " + logadoOpt.get().getNome() + ". Aguardando Efetivação do ADMIN.");
            
            aprovacaoRepository.save(aprov);
            return ResponseEntity.ok().body(Map.of("status", "EM_EFETIVACAO", "message", "Sua solicitação de exclusão foi enviada para efetivação do ADMIN."));
        }

        Optional<Marcacao> existente = marcacaoRepository.findByMatriculaAndData(matricula, localDate);
        if (existente.isPresent()) {
            marcacaoRepository.delete(existente.get());
        }
        return ResponseEntity.ok().body(Map.of("status", "SUCESSO", "message", "Marcação excluída com sucesso."));
    }
}
