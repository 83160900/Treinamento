package com.treinamento.controller;

import com.treinamento.model.Feriado;
import com.treinamento.repository.FeriadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feriados")
public class FeriadoController {

    @Autowired
    private FeriadoRepository feriadoRepository;

    @GetMapping
    public List<Feriado> listar(@RequestParam(required = false) Integer ano) {
        if (ano != null) {
            return feriadoRepository.findByAnoOrderByData(ano);
        }
        return feriadoRepository.findAll();
    }

    @PostMapping
    public Feriado salvar(@RequestBody Feriado feriado) {
        if (feriado.getData() != null) {
            feriado.setAno(feriado.getData().getYear());
        }
        return feriadoRepository.save(feriado);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        feriadoRepository.deleteById(id);
    }
}
