package com.treinamento.controller;

import com.treinamento.model.Horario;
import com.treinamento.repository.HorarioRepository;
import com.treinamento.repository.FilialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private FilialRepository filialRepository;

    @GetMapping
    public List<Horario> listar(@RequestParam(required = false) String filial) {
        if (filial != null && !filial.isEmpty()) {
            return horarioRepository.findByFilial(filial);
        }
        return horarioRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Horario horario) {
        // Validação da Filial
        if (horario.getFilial() != null && !horario.getFilial().isEmpty() && !filialRepository.existsById(horario.getFilial())) {
            return ResponseEntity.status(400).body("Filial nao encontrada: " + horario.getFilial());
        }
        return ResponseEntity.ok(horarioRepository.save(horario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        horarioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
