package com.trendsit.trendsit_fase2.service.profile;

import com.trendsit.trendsit_fase2.dto.postagem.PostagemResponseDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfileAdminDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfileAdminUpdateDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfilePublicoDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfileRequestDTO;
import com.trendsit.trendsit_fase2.dto.auth.AuthProfileDTO;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface ProfileService {

    Profile updateUserProfileAdmin(UUID userId, ProfileRequestDTO request);
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
    List<ProfileAdminDTO> findAllForAdmin();
    List<ProfilePublicoDTO> findAllPublicoProfiles();
    Profile updateUserRole(UUID userId, ProfileRole newRole);
    Profile atualizarPerfilAdmin(UUID userId, ProfileAdminUpdateDTO dto);
    @Transactional
    void updateLastActive(UUID userId);
    List<PostagemResponseDTO> findPostsForUser(UUID userId);
}