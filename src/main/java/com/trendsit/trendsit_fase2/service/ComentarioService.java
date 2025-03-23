package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.ComentarioDto;
import com.trendsit.trendsit_fase2.dto.ComentarioResponseDTO;
import com.trendsit.trendsit_fase2.model.Comentario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComentarioService {
    Comentario adicionarComentario(ComentarioDto comentarioDto, UUID autorId, Long postId);
    Optional<Comentario> findById(Long commentId);
    boolean isOwnerOrAdmin(Comentario comentario, UUID currentUserId);
    Comentario updateComentario(Long commentId, ComentarioDto comentarioDto);
    void deleteComentario(Long postId, Long commentId, UUID currentUserId);
    List<ComentarioResponseDTO> findByPostIdAndAutorId(Long postId, UUID autorId);
    List<ComentarioResponseDTO> findComentariosByAutorId(UUID autorId);
}