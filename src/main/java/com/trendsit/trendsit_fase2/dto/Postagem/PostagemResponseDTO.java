package com.trendsit.trendsit_fase2.dto.Postagem;

import com.trendsit.trendsit_fase2.dto.AutorDTO;
import com.trendsit.trendsit_fase2.dto.ComentarioResponseDTO;
import com.trendsit.trendsit_fase2.model.Postagem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostagemResponseDTO {
    private Long id;
    private String titulo;
    private String conteudo;
    private LocalDateTime createdAt;
    private AutorDTO autor;
    private int quantidadeComentarios;
    private List<ComentarioResponseDTO> comentarios;

    public PostagemResponseDTO(Postagem postagem) {
        this.titulo = postagem.getTitulo();
        this.conteudo = postagem.getConteudo();
        this.createdAt = postagem.getCreatedAt();
        this.autor = new AutorDTO(postagem.getAutor());
        this.quantidadeComentarios = postagem.getComentarios().size();

        this.comentarios = postagem.getComentarios().stream()
                .map(comentario -> new ComentarioResponseDTO(
                        comentario.getConteudo(),
                        comentario.getCreatedAt(),
                        new AutorDTO(comentario.getAutor())
                ))
                .toList();
    }

}

