package com.trendsit.trendsit_fase2.controller.comentario;

import com.trendsit.trendsit_fase2.dto.comentario.ComentarioDTO;
import com.trendsit.trendsit_fase2.dto.comentario.ComentarioResponseDTO;
import com.trendsit.trendsit_fase2.model.comentario.Comentario;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.service.comentario.ComentarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ComentarioResponseDTO> adicionarComentario(
            @PathVariable Long postId,
            @Valid @RequestBody ComentarioDTO comentarioDto,
            @AuthenticationPrincipal Profile profile
    ) {
        Comentario novoComentario = comentarioService.adicionarComentario(
                comentarioDto,
                profile.getId(),
                postId
        );
        return ResponseEntity.ok(new ComentarioResponseDTO(novoComentario)); // Return DTO
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Comentario> atualizarComentario(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody ComentarioDTO comentarioDto,
            @AuthenticationPrincipal Profile profile
    ) {
        Comentario comentarioAtualizado = comentarioService.updateComentario(
                commentId,
                postId,
                comentarioDto,
                profile.getId()
        );
        return ResponseEntity.ok(comentarioAtualizado);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deletarComentario(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal Profile profile
    ) {
        comentarioService.deleteComentario(postId, commentId, profile.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ComentarioResponseDTO>> getComentarios(@PathVariable Long postId) {
        List<ComentarioResponseDTO> comentarios = comentarioService.findByPostagemId(postId);
        return ResponseEntity.ok(comentarios);
    }
}
