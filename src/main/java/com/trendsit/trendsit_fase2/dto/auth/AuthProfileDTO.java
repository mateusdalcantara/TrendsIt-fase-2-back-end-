package com.trendsit.trendsit_fase2.dto.auth;

import com.trendsit.trendsit_fase2.model.profile.ProfileRole;

import java.util.UUID;

public record AuthProfileDTO(
        UUID id,
        String username,
        ProfileRole role
) {
    public AuthProfileDTO(UUID id, String username, ProfileRole role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}