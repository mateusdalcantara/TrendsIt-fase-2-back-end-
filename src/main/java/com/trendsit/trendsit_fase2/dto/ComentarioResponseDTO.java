package com.trendsit.trendsit_fase2.dto;

import com.trendsit.trendsit_fase2.model.Comentario;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ComentarioResponseDTO {
    private Long id;
    private String conteudo;
    private LocalDateTime createdAt;
    private AutorDTO autor;
    private Long postagemId;

    public ComentarioResponseDTO(Comentario comentario) {
        this.id = comentario.getId();
        this.conteudo = comentario.getConteudo();
        this.createdAt = comentario.getCreatedAt();
        this.autor = new AutorDTO(comentario.getAutor());
        this.postagemId = comentario.getPostagem().getId();
    }

    public ComentarioResponseDTO(Long id, String conteudo, LocalDateTime createdAt,
                                 AutorDTO autor, Long postagemId) {
        this.id = id;
        this.conteudo = conteudo;
        this.createdAt = createdAt;
        this.autor = autor;
        this.postagemId = postagemId;
    }

}