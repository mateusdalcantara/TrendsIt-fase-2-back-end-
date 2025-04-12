package com.trendsit.trendsit_fase2.dto.relationship;

import com.trendsit.trendsit_fase2.model.friendship.Friendship;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class FriendshipResponseDTO {
    private final String status;
    private final String friendUsername;
    private final String friendProfileImageUrl;
    private final LocalDateTime createdAt;

    public FriendshipResponseDTO(Friendship friendship, Profile currentUser) {
        this.status = friendship.getStatus().name();
        this.createdAt = friendship.getCreatedAt();

        Profile friend = friendship.getUserFrom().equals(currentUser)
                ? friendship.getUserTo()
                : friendship.getUserFrom();

        this.friendUsername = friend.getUsername();
        this.friendProfileImageUrl = friend.getProfileImage();
    }
}