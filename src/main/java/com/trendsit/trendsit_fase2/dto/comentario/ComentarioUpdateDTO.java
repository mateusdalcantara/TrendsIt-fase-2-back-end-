package com.trendsit.trendsit_fase2.dto.comentario;

import lombok.Getter;

@Getter
public class ComentarioUpdateDTO {
    private String conteudo;

    public ComentarioUpdateDTO(String conteudo) {
        this.conteudo = conteudo;
    }
}
