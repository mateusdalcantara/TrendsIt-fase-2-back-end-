package com.trendsit.trendsit_fase2.dto.postagem;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trendsit.trendsit_fase2.model.postagem.Postagem;
import lombok.Getter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PostagemResponseDTO {
    private Long id;
    private String conteudo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    private String autorUsername;

    public PostagemResponseDTO(Postagem postagem) {
        this.id = postagem.getId();
        this.conteudo = postagem.getConteudo();
        this.createdAt = postagem.getCreatedAt();
        this.autorUsername = postagem.getAutor().getUsername(); // Garanta que postagem.getAutor() não é null
    }
}