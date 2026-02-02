package com.treinamento.controller;

import com.treinamento.model.Aprovacao;
import com.treinamento.model.Marcacao;
import com.treinamento.repository.AprovacaoRepository;
import com.treinamento.repository.MarcacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/aprovacoes")
public class AprovacaoController {

    @Autowired
    private AprovacaoRepository aprovacaoRepository;

    @Autowired
    private MarcacaoRepository marcacaoRepository;

    @Autowired
    private com.treinamento.repository.FuncionarioRepository funcionarioRepository;

    @GetMapping
    public List<Aprovacao> listar(@RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return aprovacaoRepository.findByStatus(status);
        }
        return aprovacaoRepository.findAll();
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovar(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioCpf = auth.getName();
        Optional<com.treinamento.model.Funcionario> funcOpt = funcionarioRepository.findByCpf(usuarioCpf);
        
        if (funcOpt.isEmpty()) return ResponseEntity.status(403).build();
        String perfil = funcOpt.get().getPerfil();

        Optional<Aprovacao> opt = aprovacaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Aprovacao aprovacao = opt.get();
        
        // Regra de Alçada
        if ("GESTOR".equals(perfil)) {
            if (!"PENDENTE".equals(aprovacao.getStatus())) {
                return ResponseEntity.badRequest().body("Esta solicitação não está pendente de aprovação do gestor.");
            }
            aprovacao.setStatus("EM_EFETIVACAO");
            aprovacao.setAprovadorCpf(usuarioCpf);
            aprovacao.setDataAprovacao(LocalDateTime.now());
            aprovacao.addHistorico("Aprovado pelo Gestor: " + funcOpt.get().getNome());
            aprovacaoRepository.save(aprovacao);
            return ResponseEntity.ok().build();
        } 
        
        if ("ADMIN".equals(perfil)) {
            if (!"PENDENTE".equals(aprovacao.getStatus()) && !"EM_EFETIVACAO".equals(aprovacao.getStatus())) {
                return ResponseEntity.badRequest().body("Esta solicitação já foi processada.");
            }
            
            // Se o ADMIN aprova algo que estava PENDENTE (pulando o gestor ou gestor incluiu), 
            // ou algo que estava EM_EFETIVACAO.
            
            // Aplica a mudança na tabela de marcações
            if ("EXCLUSAO".equals(aprovacao.getTipo())) {
                Optional<Marcacao> marcacaoOpt = marcacaoRepository.findByMatriculaAndData(
                    aprovacao.getMatricula(), 
                    aprovacao.getDataMarcacao()
                );
                marcacaoOpt.ifPresent(marcacaoRepository::delete);
            } else {
                // INCLUSAO, ALTERACAO ou ABONO
                Optional<Marcacao> existenteOpt = marcacaoRepository.findByMatriculaAndData(
                    aprovacao.getMatricula(), 
                    aprovacao.getDataMarcacao()
                );

                Marcacao m;
                if (existenteOpt.isPresent()) {
                    m = existenteOpt.get();
                } else {
                    m = new Marcacao();
                    m.setFilial(aprovacao.getFilial());
                    m.setMatricula(aprovacao.getMatricula());
                    m.setData(aprovacao.getDataMarcacao());
                }

                if ("ABONO".equals(aprovacao.getTipo())) {
                    m.setIdAbono(aprovacao.getIdAbono());
                    m.setHorasAbonadas(aprovacao.getHorasAbonadas());
                    m.setAbonoDiaTodo(aprovacao.isAbonoDiaTodo());
                    m.setJustificativa(aprovacao.getJustificativa());
                    m.setAnexoNome(aprovacao.getAnexoNome());
                    m.setOrigem("A"); // A de Aprovado
                } else {
                    // INCLUSAO ou ALTERACAO
                    if (aprovacao.getE1() != null) m.setE1(aprovacao.getE1());
                    if (aprovacao.getS1() != null) m.setS1(aprovacao.getS1());
                    if (aprovacao.getE2() != null) m.setE2(aprovacao.getE2());
                    if (aprovacao.getS2() != null) m.setS2(aprovacao.getS2());
                    m.setOrigem("A"); // A de Aprovado
                }
                marcacaoRepository.save(m);
            }

            aprovacao.setStatus("APROVADO");
            aprovacao.setEfetivadorCpf(usuarioCpf);
            aprovacao.setDataEfetivacao(LocalDateTime.now());
            aprovacao.addHistorico("Efetivado pelo ADMIN: " + funcOpt.get().getNome());
            aprovacaoRepository.save(aprovacao);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(403).body("Perfil sem permissão para aprovar.");
    }

    @PostMapping("/{id}/rejeitar")
    public ResponseEntity<?> rejeitar(@PathVariable Long id, @RequestBody(required = false) String justificativa) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioCpf = auth.getName();
        Optional<com.treinamento.model.Funcionario> funcOpt = funcionarioRepository.findByCpf(usuarioCpf);
        
        if (funcOpt.isEmpty()) return ResponseEntity.status(403).build();
        String perfil = funcOpt.get().getPerfil();

        Optional<Aprovacao> opt = aprovacaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Aprovacao aprovacao = opt.get();
        
        if ("GESTOR".equals(perfil)) {
            if (!"PENDENTE".equals(aprovacao.getStatus())) {
                return ResponseEntity.badRequest().body("Esta solicitação não pode ser rejeitada pelo gestor.");
            }
            aprovacao.setStatus("REJEITADO");
            aprovacao.setAprovadorCpf(usuarioCpf);
            aprovacao.setDataAprovacao(LocalDateTime.now());
            if (justificativa != null) aprovacao.setJustificativa(justificativa);
            aprovacao.addHistorico("Rejeitado pelo Gestor: " + funcOpt.get().getNome() + (justificativa != null ? " - Motivo: " + justificativa : ""));
            aprovacaoRepository.save(aprovacao);
            return ResponseEntity.ok().build();
        }

        if ("ADMIN".equals(perfil)) {
            // ADMIN pode recusar PENDENTE ou EM_EFETIVACAO. 
            // Se recusar EM_EFETIVACAO (já aprovada pelo gestor), volta para PENDENTE com justificativa?
            // "caso o ADMIN recuse uma solictação ja aprovada, deve-se retornar para origem inicial com uma justificatica da possivel recusa"
            if ("EM_EFETIVACAO".equals(aprovacao.getStatus())) {
                aprovacao.setStatus("PENDENTE");
                aprovacao.addHistorico("Recusado pelo ADMIN (retornou ao gestor): " + funcOpt.get().getNome() + (justificativa != null ? " - Motivo: " + justificativa : ""));
                // No caso de retornar, talvez queiramos guardar a justificativa do admin em algum lugar ou no histórico.
                // O campo justificativa geral é usado pelo solicitante ou gestor.
                aprovacaoRepository.save(aprovacao);
                return ResponseEntity.ok().build();
            } else if ("PENDENTE".equals(aprovacao.getStatus())) {
                aprovacao.setStatus("REJEITADO");
                aprovacao.setEfetivadorCpf(usuarioCpf);
                aprovacao.setDataEfetivacao(LocalDateTime.now());
                if (justificativa != null) aprovacao.setJustificativa(justificativa);
                aprovacao.addHistorico("Rejeitado pelo ADMIN: " + funcOpt.get().getNome() + (justificativa != null ? " - Motivo: " + justificativa : ""));
                aprovacaoRepository.save(aprovacao);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("Esta solicitação não pode ser rejeitada.");
            }
        }

        return ResponseEntity.status(403).body("Perfil sem permissão para rejeitar.");
    }

    @GetMapping("/arquivo/{tipo}/{nome}")
    public ResponseEntity<Resource> verArquivo(@PathVariable String tipo, @PathVariable String nome) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String cpf = auth.getName();
        Optional<com.treinamento.model.Funcionario> funcOpt = funcionarioRepository.findByCpf(cpf);
        
        if (funcOpt.isEmpty()) return ResponseEntity.status(403).build();
        String perfil = funcOpt.get().getPerfil();

        // Regra: GESTOR e ADMIN podem ver. Somente ADMIN pode baixar (tipo=download)
        if (!"ADMIN".equals(perfil) && !"GESTOR".equals(perfil)) {
            return ResponseEntity.status(403).build();
        }

        if ("download".equals(tipo) && !"ADMIN".equals(perfil)) {
            return ResponseEntity.status(403).build();
        }

        try {
            Path filePath = Paths.get("uploads").resolve(nome).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                String contentType = "application/octet-stream";
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (Exception e) {}

                var builder = ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType));
                
                if ("download".equals(tipo)) {
                    builder.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
                } else {
                    builder.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"");
                }
                
                return builder.body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
