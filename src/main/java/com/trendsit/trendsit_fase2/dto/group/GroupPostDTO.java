package com.trendsit.trendsit_fase2.dto.group;

import com.trendsit.trendsit_fase2.service.group.GroupPost;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class GroupPostDTO {
    private final UUID id;
    private final String groupName;
    private final String content;
    private final LocalDateTime createdAt;
    private final UUID authorId;
    private final String authorUsername;
    private final List<GroupPostCommentDTO> comments;

    public GroupPostDTO(GroupPost post) {
        this.id = post.getId();
        this.groupName = post.getGroup().getNome();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.authorId = post.getAuthor().getId();
        this.authorUsername = post.getAuthor().getUsername();
        this.comments = post.getComments().stream()
                .map(GroupPostCommentDTO::new)
                .collect(Collectors.toList());
    }
}
