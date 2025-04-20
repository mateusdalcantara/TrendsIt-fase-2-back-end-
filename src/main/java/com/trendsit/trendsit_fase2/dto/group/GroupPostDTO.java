package com.trendsit.trendsit_fase2.dto.group;

import com.trendsit.trendsit_fase2.service.group.GroupPost;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GroupPostDTO {
    private UUID id;
    private String content;
    private LocalDateTime createdAt;
    private UUID authorId;
    private String authorUsername;
    private List<GroupPostCommentDTO> comments; // Nova propriedade

    public GroupPostDTO(GroupPost post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.authorId = post.getAuthor().getId();
        this.authorUsername = post.getAuthor().getUsername();

        // Mapeia os comentários
        this.comments = post.getComments().stream()
                .map(GroupPostCommentDTO::new)
                .toList();
    }
}