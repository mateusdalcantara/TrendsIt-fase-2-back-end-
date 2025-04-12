package com.trendsit.trendsit_fase2.dto.relationship;

import com.trendsit.trendsit_fase2.model.friendship.Friendship;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AdminFriendshipResponseDTO extends FriendshipResponseDTO {
    private final UUID friendUserId;

    public AdminFriendshipResponseDTO(Friendship friendship, Profile currentUser) {
        super(friendship, currentUser);
        Profile friend = friendship.getUserFrom().equals(currentUser)
                ? friendship.getUserTo()
                : friendship.getUserFrom();
        this.friendUserId = friend.getId();
    }
}
