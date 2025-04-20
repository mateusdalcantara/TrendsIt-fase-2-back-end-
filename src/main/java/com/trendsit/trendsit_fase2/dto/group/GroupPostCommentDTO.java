package com.trendsit.trendsit_fase2.dto.group;

import com.trendsit.trendsit_fase2.service.group.GroupPostComment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class GroupPostCommentDTO {
    private UUID id;
    private String content;
    private UUID authorId;
    private String authorUsername;
    private LocalDateTime createdAt;

    public GroupPostCommentDTO(GroupPostComment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorId = comment.getAuthor().getId();
        this.authorUsername = comment.getAuthor().getUsername();
        this.createdAt = comment.getCreatedAt();
    }
}
