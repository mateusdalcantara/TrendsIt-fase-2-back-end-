package com.trendsit.trendsit_fase2.controller;

import com.trendsit.trendsit_fase2.service.SupabaseAuthService;
import com.trendsit.trendsit_fase2.dto.AuthRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SupabaseAuthService supabaseAuthService;

    public AuthController(SupabaseAuthService supabaseAuthService) {
        this.supabaseAuthService = supabaseAuthService;
    }

    // Registro
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("E-mail e senha são obrigatórios.");
        }
        return supabaseAuthService.registro(request.getEmail(), request.getPassword());
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            ResponseEntity<String> response = supabaseAuthService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errorResponse = e.getResponseBodyAsString();
                if (errorResponse.contains("email_not_confirmed")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Confirme seu e-mail antes de fazer login. Verifique sua caixa de entrada.");
                }
            }
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno.");
        }
    }

    // Recuperação de senha
    @PostMapping("/recover")
    public ResponseEntity<String> recoverPassword(@RequestBody AuthRequest request) {
        if (request.getEmail() == null) {
            return ResponseEntity.badRequest().body("E-mail é obrigatório.");
        }
        return supabaseAuthService.resetPassword(request.getEmail());
    }
}