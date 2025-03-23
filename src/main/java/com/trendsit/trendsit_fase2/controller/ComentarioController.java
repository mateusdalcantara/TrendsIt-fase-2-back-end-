package com.trendsit.trendsit_fase2.controller;

import com.trendsit.trendsit_fase2.dto.ComentarioDto;
import com.trendsit.trendsit_fase2.dto.ComentarioResponseDTO;
import com.trendsit.trendsit_fase2.model.Comentario;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.service.ComentarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/post/{postId}/comentario")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @PostMapping
    public ResponseEntity<Comentario> adicionarComentario(
            @PathVariable Long postId,
            @Valid @RequestBody ComentarioDto comentarioDto,
            @AuthenticationPrincipal Profile profile
    ) {
        Comentario novoComentario = comentarioService.adicionarComentario(
                comentarioDto,
                profile.getId(),
                postId
        );
        return ResponseEntity.ok(novoComentario);
    }

    @PutMapping("/{comentarioId}")
    public ResponseEntity<Comentario> updateComentario(
            @PathVariable Long postId,
            @PathVariable Long comentarioId,
            @Valid @RequestBody ComentarioDto comentarioDto,
            @AuthenticationPrincipal Profile currentUser
    ) {
        Comentario comentario = comentarioService.findById(comentarioId)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));

        // Validate comment belongs to the post
        if (!comentario.getPostagem().getId().equals(postId)) {
            throw new IllegalArgumentException("Comentário não pertence ao post");
        }

        // Check ownership/admin
        if (!comentarioService.isOwnerOrAdmin(comentario, currentUser.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }

        Comentario updatedComment = comentarioService.updateComentario(comentarioId, comentarioDto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{comentarioId}")
    public ResponseEntity<Void> deleteComentario(
            @PathVariable Long postId,
            @PathVariable Long comentarioId,
            @AuthenticationPrincipal Profile currentUser
    ) {
        comentarioService.deleteComentario(postId, comentarioId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/meus-comentarios")
    public ResponseEntity<List<ComentarioResponseDTO>> getMyComments(
            @AuthenticationPrincipal Profile currentUser
    ) {
        List<ComentarioResponseDTO> comentarios = comentarioService.findComentariosByAutorId(currentUser.getId());
        return ResponseEntity.ok(comentarios);
    }
}
