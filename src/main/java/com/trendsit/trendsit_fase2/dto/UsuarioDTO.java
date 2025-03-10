package com.trendsit.trendsit_fase2.dto;

import java.time.LocalDateTime;

public record UsuarioDTO(Long id,
                         String Nome,
                         String Email,
                         String Curso,
                         String Role,
                         LocalDateTime d_criacao) {
}