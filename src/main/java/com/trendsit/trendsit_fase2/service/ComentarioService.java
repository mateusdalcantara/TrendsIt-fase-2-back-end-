package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.ComentarioDTO;
import com.trendsit.trendsit_fase2.dto.ComentarioResponseDTO;
import com.trendsit.trendsit_fase2.model.Comentario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComentarioService {

    Comentario adicionarComentario(ComentarioDTO comentarioDTO, UUID autorId, Long postId);
    Optional<Comentario> findById(Long comentarioId);
    boolean isOwnerOrAdmin(Comentario comentario, UUID currentUserId);
    List<ComentarioResponseDTO> findByPostIdAndAutorId(Long postId, UUID autorId);
    List<ComentarioResponseDTO> findComentariosByAutorId(UUID autorId);
    List<ComentarioResponseDTO> findByPostagemId(Long postId);
    Comentario updateComentario(Long comentarioId, Long postId, ComentarioDTO comentarioDTO, UUID currentUserId);
    void deleteComentario(Long postId, Long comentarioId, UUID currentUserId);
}