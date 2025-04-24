package com.trendsit.trendsit_fase2.dto.comentario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ComentarioDTO {
    private Long id;
    private String conteudo;
    private LocalDateTime createdAt;
    private Long postagemId;

    public ComentarioDTO(Long id, String conteudo, LocalDateTime createdAt, Long postagemId) {
        this.id = id;
        this.conteudo = conteudo;
        this.createdAt = createdAt;
        this.postagemId = postagemId;
    }

}
