package com.trendsit.trendsit_fase2.dto.postagem;

import com.trendsit.trendsit_fase2.dto.comentario.ComentarioResponseDTO;
import lombok.Getter;
import java.util.List;

@Getter
public class PostagemWithCommentsDTO {
    private final PostagemResponseDTO postagem;
    private final List<ComentarioResponseDTO> comentarios;

    public PostagemWithCommentsDTO(
            PostagemResponseDTO postagem,
            List<ComentarioResponseDTO> comentarios
    ) {
        this.postagem    = postagem;
        this.comentarios = comentarios;
    }
}
