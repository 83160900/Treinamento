package com.treinamento.controller;

import com.treinamento.model.Empresa;
import com.treinamento.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @GetMapping
    public List<Empresa> listar() {
        return empresaRepository.findAll();
    }

    @PostMapping
    public Empresa salvar(@RequestBody Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable String id) {
        empresaRepository.deleteById(id);
    }
}
