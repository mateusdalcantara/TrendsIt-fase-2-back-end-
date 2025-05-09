package com.trendsit.trendsit_fase2.controller.comentario;

import com.trendsit.trendsit_fase2.dto.comentario.ComentarioCreateDTO;
import com.trendsit.trendsit_fase2.dto.comentario.ComentarioDTO;
import com.trendsit.trendsit_fase2.dto.comentario.ComentarioResponseDTO;
import com.trendsit.trendsit_fase2.dto.comentario.ComentarioUpdateDTO;
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
    @PreAuthorize("hasAnyRole('ALUNO', 'ADMIN')")
    public ResponseEntity<ComentarioResponseDTO> adicionarComentario(
            @PathVariable Long postId,
            @Valid @RequestBody ComentarioCreateDTO comentarioDto,
            @AuthenticationPrincipal Profile profile
    ) {
        Comentario novoComentario = comentarioService.adicionarComentario(
                comentarioDto.getConteudo(),
                profile.getId(),
                postId
        );
        return ResponseEntity.ok(new ComentarioResponseDTO(novoComentario));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'ADMIN')")
    public ResponseEntity<ComentarioResponseDTO> atualizarComentario( // Alterado para retornar o DTO
                                                                      @PathVariable Long postId,
                                                                      @PathVariable Long commentId,
                                                                      @Valid @RequestBody ComentarioUpdateDTO comentarioUpdateDto,
                                                                      @AuthenticationPrincipal Profile profile
    ) {
        Comentario comentarioAtualizado = comentarioService.updateComentario(
                commentId,
                postId,
                comentarioUpdateDto,
                profile.getId()
        );
        return ResponseEntity.ok(new ComentarioResponseDTO(comentarioAtualizado)); // Convertendo para DTO
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'ADMIN')")
    public ResponseEntity<Void> deletarComentario(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal Profile currentUser
    ) {
        comentarioService.deleteComentario(postId, commentId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/meus")
    @PreAuthorize("hasAnyRole('ALUNO','PROFESSOR','ADMIN')")
    public ResponseEntity<List<ComentarioResponseDTO>> listarMeusComentarios(
            @AuthenticationPrincipal Profile profile
    ) {
        List<ComentarioResponseDTO> meus = comentarioService
                .findComentariosByAutorId(profile.getId());
        return ResponseEntity.ok(meus);
    }
}
