package com.trendsit.trendsit_fase2.service.profile;

import com.trendsit.trendsit_fase2.dto.diretorio.DiretorioDTO;
import com.trendsit.trendsit_fase2.dto.postagem.PostagemResponseDTO;
import com.trendsit.trendsit_fase2.dto.profile.*;
import com.trendsit.trendsit_fase2.dto.auth.AuthProfileDTO;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface ProfileService {

    List<String> obterCaminhoMaisCurto(UUID origemId, UUID destinoId);
    Optional<Profile> findById(UUID id);
    boolean existsById(UUID id);
    void createDefaultProfile(UUID userId);
    Profile createProfile(UUID userId, String username);
    Profile criarPerfil(ProfileRequestDTO request);
    Optional<AuthProfileDTO> findAuthProfileById(UUID userId);
    Profile updateProfile(UUID profileId, ProfileRequestDTO request);
    void deleteProfile(UUID userId);
    ProfileRequestDTO convertToDto(Profile profile);
    List<ProfileRequestDTO> findAllProfiles();
    List<ProfileAdminDTO> findAllForAdmin();
    List<ProfilePublicoDTO> findAllPublicoProfiles();
    List<DiretorioDTO> findAllDiretorio();
    Profile atualizarPerfilAdmin(UUID userId, ProfileAdminUpdateDTO dto);
    Profile updateUserProfileAdmin(UUID userId, ProfileRequestDTO request);
    Optional<Profile> findByUsername(String username);

}