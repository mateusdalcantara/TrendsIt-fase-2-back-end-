package com.trendsit.trendsit_fase2.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {

    // Rota acessível apenas para ADMIN
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "Acesso concedido à área administrativa!";
    }

    // Rota acessível para qualquer usuário autenticado
    @GetMapping("/protected-endpoint")
    public String getProtectedData() {
        return "Você tem acesso à rota protegida!";
    }

    // Rota acessível para ADMIN ou USER
    @GetMapping("/common-area")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String commonArea() {
        return "Área comum para usuários registrados";
    }
}