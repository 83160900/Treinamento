package com.treinamento.controller;

import com.treinamento.model.Filial;
import com.treinamento.repository.FilialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filiais")
public class FilialController {

    @Autowired
    private FilialRepository filialRepository;

    @GetMapping
    public List<Filial> listar() {
        return filialRepository.findAll();
    }

    @PostMapping
    public Filial salvar(@RequestBody Filial filial) {
        return filialRepository.save(filial);
    }

    @DeleteMapping("/{codFilial}")
    public ResponseEntity<Void> excluir(@PathVariable String codFilial) {
        filialRepository.deleteById(codFilial);
        return ResponseEntity.ok().build();
    }
}
