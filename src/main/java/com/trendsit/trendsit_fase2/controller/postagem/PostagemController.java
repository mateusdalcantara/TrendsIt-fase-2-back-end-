package com.trendsit.trendsit_fase2.controller.postagem;

import com.trendsit.trendsit_fase2.dto.postagem.*;
import com.trendsit.trendsit_fase2.model.postagem.Postagem;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.service.postagem.PostagemService;
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
import com.trendsit.trendsit_fase2.dto.postagem.PostagemRequestDTO;
import com.trendsit.trendsit_fase2.dto.postagem.PostagemResponseDTO;


import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/post")
public class PostagemController {

    private final PostagemService postagemService;

    public PostagemController(PostagemService postagemService) {
        this.postagemService = postagemService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ALUNO', 'ADMIN')")
    public ResponseEntity<PostagemResponseDTO> createPost(
            @Valid @RequestBody PostagemRequestDTO postagemRequest,
            @AuthenticationPrincipal Profile profile
    ) {
        Postagem postagem = postagemService.createPost(postagemRequest, profile.getId());
        PostagemResponseDTO responseDTO = new PostagemResponseDTO(postagem); // Use o construtor que recebe a entidade
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<PostagemWithCommentsDTO>> getAllPosts() {
        List<PostagemWithCommentsDTO> dtos = postagemService.findAllPostsWithComments();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/admin/Posts")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<PostagemResponseAdminDTO>> getAllPostsAdmin() {
        List<PostagemResponseAdminDTO> posts = postagemService.findAllPostsAdmin();
        return ResponseEntity.ok(posts);
    }


    // PostagemController.java
    @PutMapping("/{postId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'ADMIN')")
    public ResponseEntity<PostagemResponseDTO> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostagemUpdateDTO postagemUpdateDTO, // Use PostagemUpdateDTO
            @AuthenticationPrincipal Profile currentUser
    ) {
        Postagem postagem = postagemService.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        if (!postagemService.isOwnerOrAdmin(postagem, currentUser.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }

        Postagem updatedPost = postagemService.updatePostContent(postId, postagemUpdateDTO);
        return ResponseEntity.ok(new PostagemResponseDTO(updatedPost));
    }

    @DeleteMapping("/api/post/{postId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'ADMIN')")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Profile currentUser
    ) {
        Postagem postagem = postagemService.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Check if user is owner or ADMIN
        if (!postagem.getAutor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(ProfileRole.ADMIN)) {
            throw new AccessDeniedException("You cannot delete this post");
        }

        postagemService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}