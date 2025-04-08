package com.trendsit.trendsit_fase2.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComentarioDTO {
    @NotBlank(message = "O conteúdo do comentário não pode estar vazio")
    private String conteudo;
}
