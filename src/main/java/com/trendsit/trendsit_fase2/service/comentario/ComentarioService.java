package com.trendsit.trendsit_fase2.service.comentario;

import com.trendsit.trendsit_fase2.dto.comentario.ComentarioDTO;
import com.trendsit.trendsit_fase2.dto.comentario.ComentarioResponseDTO;
import com.trendsit.trendsit_fase2.dto.comentario.ComentarioUpdateDTO;
import com.trendsit.trendsit_fase2.model.comentario.Comentario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface ComentarioService {

    Optional<Comentario> findById(Long comentarioId);
    boolean isOwnerOrAdmin(Comentario comentario, UUID currentUserId);
    List<ComentarioResponseDTO> findByPostIdAndAutorId(Long postId, UUID autorId);
    List<ComentarioResponseDTO> findComentariosByAutorId(UUID autorId);
    List<ComentarioResponseDTO> findByPostagemId(Long postId);

    Comentario updateComentario(
            Long comentarioId,
            Long postId,
            ComentarioUpdateDTO comentarioUpdateDTO, // Alterado o tipo do DTO
            UUID currentUserId
    );

    void deleteComentario(Long postId, Long comentarioId, UUID currentUserId);
    Comentario adicionarComentario(String conteudo, UUID autorId, Long postId);

}