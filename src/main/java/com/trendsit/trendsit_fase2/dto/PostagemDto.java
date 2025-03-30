package com.trendsit.trendsit_fase2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostagemDTO {
    @NotBlank(message = "O título não pode estar vazio")
    private String titulo;

    @NotBlank(message = "O conteúdo não pode estar vazio")
    private String conteudo;

}