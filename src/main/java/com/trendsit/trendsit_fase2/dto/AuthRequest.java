package com.trendsit.trendsit_fase2.dto;

/**
 * DTO para receber credenciais de autenticação.
 */
public class AuthRequest {
    private String email;
    private String password;

    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}