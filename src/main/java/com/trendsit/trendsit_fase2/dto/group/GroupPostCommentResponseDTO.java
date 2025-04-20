package com.trendsit.trendsit_fase2.dto.group;

import com.trendsit.trendsit_fase2.service.group.GroupPostComment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class GroupPostCommentResponseDTO {
    private UUID id;
    private String content;
    private UUID postId; // Apenas o ID da postagem
    private UUID authorId;
    private String authorUsername;
    private LocalDateTime createdAt;

    public GroupPostCommentResponseDTO(GroupPostComment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.postId = comment.getPost().getId();
        this.authorId = comment.getAuthor().getId();
        this.authorUsername = comment.getAuthor().getUsername();
        this.createdAt = comment.getCreatedAt();
    }
}