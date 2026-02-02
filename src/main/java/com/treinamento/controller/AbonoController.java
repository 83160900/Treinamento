package com.treinamento.controller;

import com.treinamento.model.Abono;
import com.treinamento.repository.AbonoRepository;
import com.treinamento.repository.FilialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/abonos")
public class AbonoController {

    @Autowired
    private AbonoRepository abonoRepository;

    @Autowired
    private FilialRepository filialRepository;

    @GetMapping
    public List<Abono> listar(@RequestParam(required = false) String filial) {
        if (filial != null && !filial.isEmpty()) {
            return abonoRepository.findByFilial(filial);
        }
        return abonoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Abono abono) {
        // Validação da Filial
        if (abono.getFilial() != null && !abono.getFilial().isEmpty() && !filialRepository.existsById(abono.getFilial())) {
            return ResponseEntity.status(400).body("Filial nao encontrada: " + abono.getFilial());
        }
        return ResponseEntity.ok(abonoRepository.save(abono));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        abonoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
