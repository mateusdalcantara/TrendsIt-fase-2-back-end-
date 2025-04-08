package com.trendsit.trendsit_fase2.dto.comentario;

import com.trendsit.trendsit_fase2.dto.autor.AutorAdminDTO;
import com.trendsit.trendsit_fase2.model.comentario.Comentario;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ComentarioResponseAdminDTO {
    private Long id;
    private String conteudo;
    private LocalDateTime createdAt;
    private AutorAdminDTO autor;
    private Long postagemId;

    public ComentarioResponseAdminDTO(Comentario comentario) {
        this.id = comentario.getId();
        this.conteudo = comentario.getConteudo();
        this.createdAt = comentario.getCreatedAt();
        this.autor = new AutorAdminDTO(comentario.getAutor());
        this.postagemId = comentario.getPostagem().getId();
    }

    public ComentarioResponseAdminDTO(Long id, String conteudo, LocalDateTime createdAt,
                                 AutorAdminDTO autor, Long postagemId) {
        this.id = id;
        this.conteudo = conteudo;
        this.createdAt = createdAt;
        this.autor = autor;
        this.postagemId = postagemId;
    }

}