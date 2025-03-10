package com.trendsit.trendsit_fase2.dto;

public record RegisterDTO(Long id,
                          String Nome,
                          String Email,
                          String Curso,
                          String Senha,
                          String Role) {
}
