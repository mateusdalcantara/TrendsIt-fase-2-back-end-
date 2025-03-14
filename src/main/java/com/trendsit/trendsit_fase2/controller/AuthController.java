package com.trendsit.trendsit_fase2.controller;

import com.trendsit.trendsit_fase2.service.SupabaseAuthService;
import com.trendsit.trendsit_fase2.dto.AuthRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Controlador para operações de autenticação:
 * - Registro de usuários
 * - Login
 * - Recuperação de senha
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SupabaseAuthService supabaseAuthService;

    public AuthController(SupabaseAuthService supabaseAuthService) {
        this.supabaseAuthService = supabaseAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("Credenciais inválidas");
        }
        return supabaseAuthService.registro(request.getEmail(), request.getPassword());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            return supabaseAuthService.login(request.getEmail(), request.getPassword());
        } catch (HttpClientErrorException e) {
            return tratarErrosAutenticacao(e);
        }
    }

    private ResponseEntity<?> tratarErrosAutenticacao(HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
            String errorBody = e.getResponseBodyAsString();
            if (errorBody.contains("email_not_confirmed")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Confirme seu e-mail antes de fazer login");
            }
        }
        return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
    }

    @PostMapping("/recover")
    public ResponseEntity<String> recoverPassword(@RequestBody AuthRequest request) {
        if (request.getEmail() == null) {
            return ResponseEntity.badRequest().body("E-mail é obrigatório");
        }
        return supabaseAuthService.resetPassword(request.getEmail());
    }
}