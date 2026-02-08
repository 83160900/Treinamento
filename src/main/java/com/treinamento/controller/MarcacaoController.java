package com.treinamento.controller;

import com.treinamento.model.Marcacao;
import com.treinamento.model.Periodo;
import com.treinamento.repository.MarcacaoRepository;
import com.treinamento.repository.PeriodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private com.treinamento.repository.EscalaRepository escalaRepository;

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
        // Para garantir que o período apareça, buscamos sem o filtro de tenant
        // pois o período pode ser "Geral" (filial vazia)
        List<Periodo> periodos = periodoRepository.findAll();
        
        Optional<Periodo> p = periodos.stream()
                .filter(Periodo::isAtivo)
                .filter(x -> filial == null || filial.isEmpty() || filial.equals(x.getFilial()) || x.getFilial() == null || x.getFilial().isEmpty())
                .findFirst();

        return p.map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/bater-ponto")
    public ResponseEntity<?> baterPonto(@RequestBody Map<String, Object> payload) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String cpfLogado = auth.getName();
        
        Optional<com.treinamento.model.Funcionario> logadoOpt = funcionarioRepository.findByCpf(cpfLogado);
        if (logadoOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuário não encontrado.");
        }
        com.treinamento.model.Funcionario func = logadoOpt.get();
        
        LocalDate hoje = LocalDate.now();
        String horaAtual = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        Double latitude = payload.get("latitude") != null ? Double.valueOf(payload.get("latitude").toString()) : null;
        Double longitude = payload.get("longitude") != null ? Double.valueOf(payload.get("longitude").toString()) : null;
        String endereco = payload.get("endereco") != null ? payload.get("endereco").toString() : null;

        // --- Lógica de Cerca Virtual ---
        boolean foraDoRaio = false;
        // A regra de cerca virtual só se aplica para COLABORADOR. GESTOR e ADMIN não se aplica.
        if ("COLABORADOR".equals(func.getPerfil()) && func.getIdEscala() != null) {
            Optional<com.treinamento.model.Escala> escalaOpt = escalaRepository.findById(func.getIdEscala());
            if (escalaOpt.isPresent() && escalaOpt.get().isCercaVirtual()) {
                com.treinamento.model.Escala escala = escalaOpt.get();
                Optional<com.treinamento.model.Filial> filialOpt = filialRepository.findById(func.getFilial());
                if (filialOpt.isPresent()) {
                    com.treinamento.model.Filial filial = filialOpt.get();
                    if (filial.getLatitude() != null && filial.getLongitude() != null && latitude != null && longitude != null) {
                        double distancia = calcularDistancia(latitude, longitude, filial.getLatitude(), filial.getLongitude());
                        if (distancia > (escala.getRaioCerca() != null ? escala.getRaioCerca() : 1000)) {
                            foraDoRaio = true;
                        }
                    }
                }
            }
        }

        if (foraDoRaio) {
            com.treinamento.model.Aprovacao aprov = new com.treinamento.model.Aprovacao();
            aprov.setFilial(func.getFilial());
            aprov.setMatricula(func.getMatricula());
            aprov.setNomeFuncionario(func.getNome());
            aprov.setDataMarcacao(hoje);
            aprov.setTipo("PONTO_FORA_RAIO");
            aprov.setSolicitanteCpf(cpfLogado);
            if (latitude != null) aprov.setLatitude(latitude);
            if (longitude != null) aprov.setLongitude(longitude);
            aprov.addHistorico("Ponto registrado fora do raio permitido da cerca virtual.");
            
            // Determina qual batida está sendo feita para salvar na aprovação
            Marcacao temp = marcacaoRepository.findByMatriculaAndData(func.getMatricula(), hoje).orElse(new Marcacao());
            if (temp.getE1() == null) aprov.setE1(horaAtual);
            else if (temp.getS1() == null) aprov.setS1(horaAtual);
            else if (temp.getE2() == null) aprov.setE2(horaAtual);
            else if (temp.getS2() == null) aprov.setS2(horaAtual);

            aprovacaoRepository.save(aprov);

            return ResponseEntity.ok(Map.of(
                "message", "Atenção: Você está fora do raio permitido. Sua marcação foi registrada mas enviada para aprovação do gestor.",
                "hora", horaAtual,
                "status", "FORA_DO_RAIO"
            ));
        }
        // --- Fim Lógica de Cerca Virtual ---

        Marcacao marcacao = marcacaoRepository.findByMatriculaAndData(func.getMatricula(), hoje)
                .orElse(new Marcacao(func.getFilial(), func.getMatricula(), hoje, null, null, null, null, "P")); // 'P' para Ponto/Mobile/Botão

        if (marcacao.getE1() == null) {
            marcacao.setE1(horaAtual);
        } else if (marcacao.getS1() == null) {
            marcacao.setS1(horaAtual);
        } else if (marcacao.getE2() == null) {
            marcacao.setE2(horaAtual);
        } else if (marcacao.getS2() == null) {
            marcacao.setS2(horaAtual);
        } else {
            return ResponseEntity.status(400).body("Limite de marcações diárias atingido.");
        }

        marcacao.setLatitude(latitude);
        marcacao.setLongitude(longitude);
        marcacao.setEndereco(endereco);
        marcacao.setOrigem("P"); // P de Ponto via botão

        marcacaoRepository.save(marcacao);
        
        return ResponseEntity.ok(Map.of(
            "message", "Ponto registrado com sucesso!",
            "hora", horaAtual,
            "marcacao", marcacao
        ));
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
                if (novaMarcacao.getLatitude() != null) existente.setLatitude(novaMarcacao.getLatitude());
                if (novaMarcacao.getLongitude() != null) existente.setLongitude(novaMarcacao.getLongitude());
                if (novaMarcacao.getEndereco() != null) existente.setEndereco(novaMarcacao.getEndereco());
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
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raio da Terra em km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // Converte para metros
    }
}
