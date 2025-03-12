package com.trendsit.trendsit_fase2.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {

    // Rota protegida
    @GetMapping("/protected-endpoint")
    public String getProtectedData() {
        // Verifica se o usuário está autenticado
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return "Você tem acesso à rota protegida!";
        } else {
            return "Você não está autenticado!";
        }
    }
}
