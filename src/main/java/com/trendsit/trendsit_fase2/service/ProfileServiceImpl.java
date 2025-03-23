package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.AuthProfileDTO;
import com.trendsit.trendsit_fase2.dto.ProfileRequest;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.model.ProfileRole;
import com.trendsit.trendsit_fase2.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return profileRepository.findById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return profileRepository.existsById(id);
    }

    @Override
    public void createDefaultProfile(UUID userId) {
        Profile profile = new Profile();
        profile.setId(userId);
        profile.setUsername("default_username");
        profile.setRole(ProfileRole.USER);
        profileRepository.save(profile);
    }

    @Override
    public Profile createProfile(UUID userId, String username) {
        Profile profile = new Profile();
        profile.setId(userId);
        profile.setUsername(username);
        profile.setRole(ProfileRole.USER);
        return profileRepository.save(profile);
    }

    @Override
    public Profile criarPerfil(ProfileRequest request) {
        Profile profile = new Profile();
        profile.setUsername(request.getUsername());
        profile.setIdade(request.getIdade());
        profile.setCurso(request.getCurso());
        profile.setRole(ProfileRole.USER);
        return profileRepository.save(profile);
    }

    @Override
    public Optional<AuthProfileDTO> findAuthProfileById(UUID userId) {
        return profileRepository.findAuthProfileById(userId);
    }

    @Override
    public Profile updateProfile(UUID profileId, ProfileRequest request) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        profile.setUsername(request.getUsername());
        profile.setIdade(request.getIdade());
        profile.setCurso(request.getCurso());

        return profileRepository.save(profile);
    }

    @Override
    public void deleteProfile(UUID profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new EntityNotFoundException("Perfil não encontrado");
        }
        profileRepository.deleteById(profileId);
    }
}