package com.treinamento.controller;

import com.treinamento.model.Escala;
import com.treinamento.repository.EscalaRepository;
import com.treinamento.repository.FilialRepository;
import com.treinamento.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/escalas")
public class EscalaController {

    @Autowired
    private EscalaRepository escalaRepository;

    @Autowired
    private FilialRepository filialRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @GetMapping
    public List<Escala> listar(@RequestParam(required = false) String filial) {
        if (filial != null && !filial.isEmpty()) {
            return escalaRepository.findByFilial(filial);
        }
        return escalaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Escala escala) {
        if (escala.getFilial() != null && !escala.getFilial().isEmpty() && !filialRepository.existsById(escala.getFilial())) {
            return ResponseEntity.status(400).body("Filial nao encontrada: " + escala.getFilial());
        }
        
        if (escala.getIdHorario() != null && !horarioRepository.existsById(escala.getIdHorario())) {
            return ResponseEntity.status(400).body("Horario nao encontrado: " + escala.getIdHorario());
        }

        return ResponseEntity.ok(escalaRepository.save(escala));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        escalaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
