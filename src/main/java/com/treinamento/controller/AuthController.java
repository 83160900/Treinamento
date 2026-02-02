package com.treinamento.controller;

import com.treinamento.model.Funcionario;
import com.treinamento.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/primeiro-acesso")
    public ResponseEntity<?> primeiroAcesso(@RequestBody Map<String, String> payload) {
        String cpf = payload.get("cpf");
        String novaSenha = payload.get("novaSenha");

        if (cpf == null || novaSenha == null) {
            return ResponseEntity.badRequest().body("CPF e nova senha são obrigatórios.");
        }

        Optional<Funcionario> optFuncionario = funcionarioRepository.findByCpf(cpf);
        if (optFuncionario.isEmpty()) {
            return ResponseEntity.status(404).body("Funcionário não encontrado.");
        }

        Funcionario funcionario = optFuncionario.get();
        if (!funcionario.isTrocarSenha()) {
            return ResponseEntity.badRequest().body("Senha já foi alterada anteriormente.");
        }

        funcionario.setSenha(passwordEncoder.encode(novaSenha));
        funcionario.setTrocarSenha(false);
        funcionarioRepository.save(funcionario);

        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
    @GetMapping("/me")
    public ResponseEntity<?> getMe(java.security.Principal principal) {
        System.out.println("[DEBUG_LOG] /api/auth/me called. Principal: " + (principal != null ? principal.getName() : "null"));
        if (principal == null) {
            return ResponseEntity.ok(Map.of("autenticado", false));
        }
        Optional<Funcionario> optFuncionario = funcionarioRepository.findByCpf(principal.getName());
        if (optFuncionario.isEmpty()) {
            System.out.println("[DEBUG_LOG] User not found in DB: " + principal.getName());
            return ResponseEntity.status(404).body("Funcionário não encontrado.");
        }
        Funcionario funcionario = optFuncionario.get();
        System.out.println("[DEBUG_LOG] User found: " + funcionario.getNome() + " Perfil: " + funcionario.getPerfil());
        return ResponseEntity.ok(Map.of(
            "autenticado", true,
            "username", funcionario.getCpf(),
            "filial", funcionario.getFilial(),
            "matricula", funcionario.getMatricula(),
            "nome", funcionario.getNome(),
            "perfil", funcionario.getPerfil(),
            "trocarSenha", funcionario.isTrocarSenha()
        ));
    }
}
