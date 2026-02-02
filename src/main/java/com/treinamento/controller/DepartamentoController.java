package com.treinamento.controller;

import com.treinamento.model.Departamento;
import com.treinamento.repository.DepartamentoRepository;
import com.treinamento.repository.FilialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private FilialRepository filialRepository;

    @GetMapping
    public List<Departamento> listar(@RequestParam(required = false) String filial) {
        if (filial != null && !filial.isEmpty()) {
            return departamentoRepository.findByFilial(filial);
        }
        return departamentoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Departamento departamento) {
        // Validação da Filial
        if (departamento.getFilial() != null && !departamento.getFilial().isEmpty() && !filialRepository.existsById(departamento.getFilial())) {
            return ResponseEntity.status(400).body("Filial nao encontrada: " + departamento.getFilial());
        }
        return ResponseEntity.ok(departamentoRepository.save(departamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        departamentoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
