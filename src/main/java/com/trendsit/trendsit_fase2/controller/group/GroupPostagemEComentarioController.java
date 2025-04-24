package com.trendsit.trendsit_fase2.controller.group;

import com.trendsit.trendsit_fase2.dto.group.GroupPostCommentRequestDTO;
import com.trendsit.trendsit_fase2.dto.group.GroupPostCommentResponseDTO;
import com.trendsit.trendsit_fase2.dto.group.GroupPostRequestDTO;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.service.group.GroupPost;
import com.trendsit.trendsit_fase2.service.group.GroupPostComment;
import com.trendsit.trendsit_fase2.dto.group.GroupPostDTO;
import com.trendsit.trendsit_fase2.service.group.GroupPostService;
import com.trendsit.trendsit_fase2.service.profile.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups/{groupId}/posts")
@RequiredArgsConstructor
public class GroupPostagemEComentarioController {

    private final GroupPostService postService;
    private final ProfileService profileService;

    // Criar postagem
    @PostMapping
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<GroupPostDTO> createPost(
            @PathVariable UUID groupId,
            @RequestBody @Valid GroupPostRequestDTO request,
            @AuthenticationPrincipal Profile author
    ) {
        GroupPost post = postService.createPost(groupId, request, author);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GroupPostDTO(post));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @AuthenticationPrincipal Profile currentUser
    ) {
        postService.deletePost(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal Profile currentUser
    ) {
        postService.deleteComment(commentId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<GroupPostDTO> updatePost(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @RequestBody @Valid GroupPostRequestDTO request,
            @AuthenticationPrincipal Profile currentUser
    ) {
        GroupPost updatedPost = postService.updatePost(groupId, postId, request.getContent(), currentUser);
        return ResponseEntity.ok(new GroupPostDTO(updatedPost));
    }

    // GroupPostagemEComentarioController.java

    @PutMapping("/{postId}/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<GroupPostCommentResponseDTO> updateComment(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @RequestBody @Valid GroupPostCommentRequestDTO request,
            @AuthenticationPrincipal Profile currentUser
    ) {
        GroupPostComment updatedComment = postService.updateComment(
                groupId,
                postId,
                commentId,
                request.getContent(),
                currentUser
        );
        return ResponseEntity.ok(new GroupPostCommentResponseDTO(updatedComment));
    }

    // Criar comentário
    @PostMapping("/{postId}/comments")
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<GroupPostCommentResponseDTO> createComment(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @RequestBody @Valid GroupPostCommentRequestDTO request,
            @AuthenticationPrincipal Profile author
    ) {
        GroupPostComment comment = postService.createComment(postId, request, author);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GroupPostCommentResponseDTO(comment));
    }

    // Listar postagens (usando DTO)
    @GetMapping
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<List<GroupPostDTO>> getGroupPosts(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal Profile user
    ) {
        List<GroupPost> posts = postService.getPostsByGroup(groupId, user);
        List<GroupPostDTO> dtos = posts.stream()
                .map(GroupPostDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}