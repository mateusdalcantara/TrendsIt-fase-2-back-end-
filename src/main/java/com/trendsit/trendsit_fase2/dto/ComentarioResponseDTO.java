package com.trendsit.trendsit_fase2.dto;

import com.trendsit.trendsit_fase2.model.Comentario;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ComentarioResponseDTO {
    private String conteudo;
    private LocalDateTime createdAt;
    private AutorDTO autor;

    public ComentarioResponseDTO(Comentario comentario) {
        this.conteudo = comentario.getConteudo();
        this.createdAt = comentario.getCreatedAt();
        this.autor = new AutorDTO(comentario.getAutor());

    }
    public ComentarioResponseDTO(String conteudo, LocalDateTime createdAt,
                                 AutorDTO autor) {
        this.conteudo = conteudo;
        this.createdAt = createdAt;
        this.autor = autor;
    }



}