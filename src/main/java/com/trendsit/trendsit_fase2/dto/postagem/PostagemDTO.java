package com.trendsit.trendsit_fase2.dto.postagem;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostagemDTO {

    @NotBlank(message = "O conteúdo não pode estar vazio")
    private String conteudo;

}