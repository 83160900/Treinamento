package com.treinamento.controller;

import com.treinamento.model.HoraExtra;
import com.treinamento.repository.HoraExtraRepository;
import com.treinamento.repository.FilialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horas-extras")
public class HoraExtraController {

    @Autowired
    private HoraExtraRepository horaExtraRepository;

    @Autowired
    private FilialRepository filialRepository;

    @GetMapping
    public List<HoraExtra> listar(@RequestParam(required = false) String filial) {
        if (filial != null && !filial.isEmpty()) {
            return horaExtraRepository.findByFilial(filial);
        }
        return horaExtraRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody HoraExtra horaExtra) {
        // Validação da Filial
        if (horaExtra.getFilial() != null && !horaExtra.getFilial().isEmpty() && !filialRepository.existsById(horaExtra.getFilial())) {
            return ResponseEntity.status(400).body("Filial nao encontrada: " + horaExtra.getFilial());
        }
        return ResponseEntity.ok(horaExtraRepository.save(horaExtra));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        horaExtraRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
