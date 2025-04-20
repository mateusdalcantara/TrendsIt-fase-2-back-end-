package com.trendsit.trendsit_fase2.dto.profile;

import com.trendsit.trendsit_fase2.model.profile.Profile;

import java.util.UUID;

public class SimpleProfileDTO {
    private UUID id;
    private String username;
    private String profileImage;
    private String role;

    public SimpleProfileDTO(Profile profile) {
        this.id = profile.getId();
        this.username = profile.getUsername();
        this.profileImage = profile.getProfileImage();
        this.role = String.valueOf(profile.getRole());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}