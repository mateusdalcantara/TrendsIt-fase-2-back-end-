package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.AuthProfileDTO;
import com.trendsit.trendsit_fase2.dto.ProfileRequest;
import com.trendsit.trendsit_fase2.model.Profile;
import java.util.Optional;
import java.util.UUID;

public interface ProfileService {
    Optional<Profile> findById(UUID id);
    boolean existsById(UUID id);
    void createDefaultProfile(UUID userId);
    Profile createProfile(UUID userId, String username);
    Profile criarPerfil(ProfileRequest request);
    Optional<AuthProfileDTO> findAuthProfileById(UUID userId);
    Profile updateProfile(UUID profileId, ProfileRequest request);
    void deleteProfile(UUID profileId);
}