package com.fitness.controller;

import com.fitness.domain.model.User;
import com.fitness.dto.LoginRequest;
import com.fitness.dto.LoginResponse;
import com.fitness.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("[DEBUG_LOG] Tentativa de login para o e-mail: " + request.getEmail());
        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    System.out.println("[DEBUG_LOG] Usuário encontrado: " + user.getEmail());
                    if (user.getPassword().equals(request.getPassword())) {
                        System.out.println("[DEBUG_LOG] Senha correta para o usuário: " + user.getEmail());
                        return ResponseEntity.ok(LoginResponse.builder()
                                .name(user.getName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .message("Login realizado com sucesso!")
                                .build());
                    } else {
                        System.out.println("[DEBUG_LOG] Senha incorreta para o usuário: " + user.getEmail());
                        return ResponseEntity.status(401).body("Credenciais inválidas!");
                    }
                })
                .orElseGet(() -> {
                    System.out.println("[DEBUG_LOG] Usuário não encontrado para o e-mail: " + request.getEmail());
                    return ResponseEntity.status(401).body("Credenciais inválidas!");
                });
    }
}
