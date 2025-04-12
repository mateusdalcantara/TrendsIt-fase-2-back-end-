package com.trendsit.trendsit_fase2.dto.relationship;

import com.trendsit.trendsit_fase2.model.friendship.Friendship;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FriendRequestDTO {
    private final Long friendshipId;
    private final String senderUsername;
    private final String receiverUsername;
    private final LocalDateTime createdAt;
    private final String status;

    public FriendRequestDTO(Friendship friendship) {
        this.friendshipId = friendship.getId();
        this.senderUsername = friendship.getUserFrom().getUsername();
        this.receiverUsername = friendship.getUserTo().getUsername();
        this.createdAt = friendship.getCreatedAt();
        this.status = friendship.getStatus().name();
    }
}