package com.trendsit.trendsit_fase2.controller;

import com.trendsit.trendsit_fase2.dto.PostagemDto;
import com.trendsit.trendsit_fase2.dto.PostagemResponseDTO;
import com.trendsit.trendsit_fase2.model.Postagem;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.service.PostagemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/post")
public class PostagemController {

    private final PostagemService postagemService;

    public PostagemController(PostagemService postagemService) {
        this.postagemService = postagemService;
    }


    @PutMapping("/{postId}")
    public ResponseEntity<PostagemResponseDTO> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostagemDto postagemDto,
            @AuthenticationPrincipal Profile currentUser
    ) {
        Postagem postagem = postagemService.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        if (!postagemService.isOwnerOrAdmin(postagem, currentUser.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }

        Postagem updatedPost = postagemService.updatePost(postId, postagemDto);
        return ResponseEntity.ok(new PostagemResponseDTO(updatedPost));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Profile currentUser
    ) {
        Postagem postagem = postagemService.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        if (!postagemService.isOwnerOrAdmin(postagem, currentUser.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }

        postagemService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PostagemResponseDTO> createPost(
            @Valid @RequestBody PostagemDto postagemDto,
            @AuthenticationPrincipal Profile profile
    ) {
        Postagem postagem = postagemService.createPost(postagemDto, profile.getId());
        return ResponseEntity.ok(new PostagemResponseDTO(postagem));
    }

    @GetMapping
    public ResponseEntity<List<PostagemResponseDTO>> getAllPosts() {
        List<PostagemResponseDTO> posts = postagemService.findAllPosts();
        return ResponseEntity.ok(posts);
    }
}