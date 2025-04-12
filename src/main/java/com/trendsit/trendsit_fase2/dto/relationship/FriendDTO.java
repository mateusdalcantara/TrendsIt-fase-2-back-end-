package com.trendsit.trendsit_fase2.dto.relationship;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FriendDTO {
    private final String username;
    private final String profileImage;
    private final Long friendNumber;

    public FriendDTO(Profile profile) {
        this.username = profile.getUsername();
        this.profileImage = profile.getProfileImage();
        this.friendNumber = profile.getFriendNumber();
    }
}