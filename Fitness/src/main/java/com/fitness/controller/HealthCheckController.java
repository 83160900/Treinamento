package com.fitness.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public Map<String, String> health() {
        return Map.of(
            "status", "Online",
            "plataforma", "Fitness",
            "mensagem", "O trem chegou à estação! O backend está funcionando corretamente."
        );
    }
}
