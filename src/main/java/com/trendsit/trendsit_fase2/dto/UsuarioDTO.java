package com.trendsit.trendsit_fase2.dto;

import lombok.Data;

@Data

public class UsuarioDTO {

    public UsuarioDTO(String nome, String email, String curso) {
        this.nome = nome;
        this.email = email;
        this.curso = curso;
    }

    private String nome;

    private String email;

    private String curso;

}
