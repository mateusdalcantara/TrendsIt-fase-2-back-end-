package com.trendsit.trendsit_fase2.dto.postagem;

import com.trendsit.trendsit_fase2.dto.autor.AutorAdminDTO;
import com.trendsit.trendsit_fase2.dto.comentario.ComentarioResponseAdminDTO;
import com.trendsit.trendsit_fase2.model.postagem.Postagem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostagemResponseAdminDTO {
    private Long id;
    private String titulo;
    private String conteudo;
    private LocalDateTime createdAt;
    private AutorAdminDTO autor;
    private int quantidadeComentarios;
    private List<ComentarioResponseAdminDTO> comentarios;

    public PostagemResponseAdminDTO(Postagem postagem) {
        this.id = postagem.getId();
        this.conteudo = postagem.getConteudo();
        this.createdAt = postagem.getCreatedAt();
        this.autor = new AutorAdminDTO(postagem.getAutor());
        this.quantidadeComentarios = postagem.getComentarios().size();
        this.comentarios = postagem.getComentarios().stream()
                .map(comentario -> new ComentarioResponseAdminDTO(
                        comentario.getId(),
                        comentario.getConteudo(),
                        comentario.getCreatedAt(),
                        new AutorAdminDTO(comentario.getAutor()),
                        comentario.getPostagem().getId()
                ))
                .toList();
    }
}

