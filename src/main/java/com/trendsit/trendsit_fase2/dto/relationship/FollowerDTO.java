package com.trendsit.trendsit_fase2.dto.relationship;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FollowerDTO {
    private final String followerUsername;
    private final LocalDateTime followedAt;

    public FollowerDTO(Profile follower) {
        this.followerUsername = follower.getUsername();
        this.followedAt = LocalDateTime.now();
    }
}
