package com.trendsit.trendsit_fase2.dto.relationship;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AdminFriendDTO extends FriendDTO {
    private final UUID id;

    public AdminFriendDTO(Profile profile) {
        super(profile);
        this.id = profile.getId();
    }
}