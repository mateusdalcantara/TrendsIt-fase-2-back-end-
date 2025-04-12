package com.trendsit.trendsit_fase2.service.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequest {
    @NotBlank(message = "Email é necessário")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Senha é necessário")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Username é necessário")
    @Size(min = 3, max = 50, message = "Nome precisa de minimo 3 a 50 caracteres")
    private String username;
}