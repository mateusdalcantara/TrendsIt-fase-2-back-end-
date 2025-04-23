package com.trendsit.trendsit_fase2.dto.comentario;

import com.trendsit.trendsit_fase2.model.comentario.Comentario;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ComentarioResponseDTO {
    private final Long id;
    private final String conteudo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private final LocalDateTime createdAt;

    private final String autorUsername;

    public ComentarioResponseDTO(Comentario c) {
        this.id = c.getId();
        this.conteudo = c.getConteudo();
        this.createdAt = c.getCreatedAt();
        this.autorUsername = c.getAutor().getUsername();
    }
}
