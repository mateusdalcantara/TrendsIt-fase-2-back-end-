package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.AuthProfileDTO;
import com.trendsit.trendsit_fase2.dto.ProfileRequestDTO;
import com.trendsit.trendsit_fase2.model.Profile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileService {
    Optional<Profile> findById(UUID id);
    boolean existsById(UUID id);
    void createDefaultProfile(UUID userId);
    Profile createProfile(UUID userId, String username);
    Profile criarPerfil(ProfileRequestDTO request);
    Optional<AuthProfileDTO> findAuthProfileById(UUID userId);
    Profile updateProfile(UUID profileId, ProfileRequestDTO request);
    void deleteProfile(UUID profileId);

    ProfileRequestDTO convertToDto(Profile profile);

    List<ProfileRequestDTO> findAllProfiles();
}