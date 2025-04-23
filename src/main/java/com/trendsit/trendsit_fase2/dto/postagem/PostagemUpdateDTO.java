package com.trendsit.trendsit_fase2.dto.postagem;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostagemUpdateDTO {
    @NotBlank(message = "O conteúdo da postagem é obrigatório")
    private String conteudo;
}
