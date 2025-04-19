package com.trendsit.trendsit_fase2.service.profile;


import com.trendsit.trendsit_fase2.dto.postagem.PostagemResponseDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfileAdminDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfileAdminUpdateDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfilePublicoDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfileRequestDTO;
import com.trendsit.trendsit_fase2.dto.auth.AuthProfileDTO;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.postagem.PostagemRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.relationship.FriendNumberService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final PostagemRepository postagemRepository;
    private final ProfileRepository profileRepository;
    private final FriendNumberService friendNumberService;

    // Injeção via construtor
    @Autowired
    public ProfileServiceImpl(
            PostagemRepository postagemRepository,
            ProfileRepository profileRepository,
            FriendNumberService friendNumberService
    ) {
        this.postagemRepository = postagemRepository;
        this.profileRepository = profileRepository;
        this.friendNumberService = friendNumberService;
    }

    @Transactional
    public void updateProfileImage(UUID userId, String image) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        profile.setProfileImage(image);
        profileRepository.save(profile);
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
        profile.setFriendNumber(friendNumberService.generateUniqueFriendNumber());
        profile.setRole(ProfileRole.USER);
        profile.setProfileImage("/default-avatar.png");
        return profileRepository.save(profile);
    }

    @Override
    public Profile criarPerfil(ProfileRequestDTO request) {
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
    public Profile updateProfile(UUID profileId, ProfileRequestDTO request) {
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

    @Override
    public ProfileRequestDTO convertToDto(Profile profile) {
        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setUsername(profile.getUsername());
        dto.setIdade(profile.getIdade());
        dto.setCurso(profile.getCurso());
        return dto;
    }

    @Override
    public List<ProfileRequestDTO> findAllProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfileAdminDTO> findAllForAdmin() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(ProfileAdminDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfilePublicoDTO> findAllPublicoProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(ProfilePublicoDTO::new)
                .collect(Collectors.toList());
    }


    @Override
    public Profile atualizarPerfilAdmin(UUID userId, ProfileAdminUpdateDTO dto) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        profile.setUsername(dto.getUsername());
        profile.setIdade(dto.getIdade());
        profile.setCurso(dto.getCurso());
        profile.setRole(dto.getRole());

        return profileRepository.save(profile);
    }

    @Override
    public Profile updateUserProfileAdmin(UUID userId, ProfileRequestDTO request) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        profile.setUsername(request.getUsername());
        profile.setIdade(request.getIdade());
        profile.setCurso(request.getCurso());

        return profileRepository.save(profile);
    }

    @Transactional
    @Override
    public void updateLastActive(UUID userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        profile.setLastActive(LocalDateTime.now());
        profileRepository.save(profile);
    }

    public boolean isUserOnline(UUID userId) {
        Profile profile = profileRepository.findById(userId).orElseThrow();
        return profile.getLastActive().isAfter(LocalDateTime.now().minusMinutes(5));
    }

    public List<PostagemResponseDTO> findPostsForUser(UUID userId) {
        Profile user = profileRepository.findById(userId).orElseThrow();
        List<UUID> followingIds = user.getFollowing().stream()
                .map(Profile::getId)
                .collect(Collectors.toList());
        followingIds.add(userId);

        return postagemRepository.findByAutor_IdIn(followingIds).stream()
                .map(PostagemResponseDTO::new)
                .collect(Collectors.toList());
    }
}
