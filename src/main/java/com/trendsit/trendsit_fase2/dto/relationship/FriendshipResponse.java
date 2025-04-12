package com.trendsit.trendsit_fase2.dto;

import com.trendsit.trendsit_fase2.model.friendship.Friendship;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class FriendshipResponse {
    private Long id;
    private UUID fromUserId;
    private UUID toUserId;
    private String status;
    private LocalDateTime createdAt;

    public FriendshipResponse(Friendship friendship) {
        this.id = friendship.getId();
        this.fromUserId = friendship.getUserFrom().getId();
        this.toUserId = friendship.getUserTo().getId();
        this.status = friendship.getStatus().name();
        this.createdAt = friendship.getCreatedAt();
    }
}