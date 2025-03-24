package com.trendsit.trendsit_fase2.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para endpoints protegidos com diferentes níveis de acesso.
 * Demonstração de autorização baseada em roles.
 */
@SecurityRequirement(name = "Bearer Authentication")
@RestController
public class ProtectedController {

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "Acesso administrativo concedido";
    }

    @GetMapping("/protected-endpoint")
    public String getProtectedData() {
        return "Conteúdo protegido";
    }

    @GetMapping("/common-area")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String commonArea() {
        return "Área comum";
    }
}