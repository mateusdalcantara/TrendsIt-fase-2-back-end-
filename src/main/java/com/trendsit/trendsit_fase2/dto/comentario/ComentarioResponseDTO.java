package com.trendsit.trendsit_fase2.dto.comentario;

import com.trendsit.trendsit_fase2.dto.autor.AutorDTO;
import com.trendsit.trendsit_fase2.model.comentario.Comentario;
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

    public ComentarioResponseDTO(Comentario comentario) {
        this.id = comentario.getId();
        this.conteudo = comentario.getConteudo();
        this.createdAt = comentario.getCreatedAt();
        this.autor = new AutorDTO(comentario.getAutor());
    }
}