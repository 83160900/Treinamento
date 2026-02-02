package com.treinamento.controller;

import com.treinamento.model.Periodo;
import com.treinamento.repository.PeriodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/periodos")
public class PeriodoController {

    @Autowired
    private PeriodoRepository periodoRepository;

    @Autowired
    private com.treinamento.repository.FilialRepository filialRepository;

    @GetMapping
    public List<Periodo> listar(@RequestParam(required = false) String filial) {
        if (filial != null && !filial.isEmpty()) {
            return periodoRepository.findByFilial(filial);
        }
        return periodoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Periodo periodo) {
        // Validação da Filial
        if (periodo.getFilial() != null && !periodo.getFilial().isEmpty() && !filialRepository.existsById(periodo.getFilial())) {
            return ResponseEntity.status(400).body("Filial nao encontrada: " + periodo.getFilial());
        }

        // Se este período for marcado como ativo, desativa os outros da mesma filial
        if (periodo.isAtivo()) {
            List<Periodo> outros;
            if (periodo.getFilial() != null) {
                outros = periodoRepository.findByFilial(periodo.getFilial());
            } else {
                outros = periodoRepository.findAll();
            }
            outros.forEach(p -> {
                if (periodo.getId() != null && p.getId().equals(periodo.getId())) {
                    return;
                }
                if (p.isAtivo()) {
                    p.setAtivo(false);
                    periodoRepository.save(p);
                }
            });
        }
        return ResponseEntity.ok(periodoRepository.save(periodo));
    }

    @GetMapping("/ativo")
    public ResponseEntity<Periodo> getAtivo(@RequestParam(required = false) String filial) {
        if (filial != null && !filial.isEmpty()) {
            return periodoRepository.findByFilialAndAtivoTrue(filial)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.noContent().build());
        }
        return periodoRepository.findByAtivoTrue()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        periodoRepository.deleteById(id);
    }
}