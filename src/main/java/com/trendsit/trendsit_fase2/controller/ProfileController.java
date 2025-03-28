package com.trendsit.trendsit_fase2.controller;

import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.model.ProfileRole;
import com.trendsit.trendsit_fase2.service.ProfileService;
import com.trendsit.trendsit_fase2.dto.ProfileRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador para gerenciamento de perfis de usuários.
 * Requer autenticação para acesso.
 */

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<List<ProfileRequestDTO>> obterPerfil(){
        List<ProfileRequestDTO> profileRequestDTOS = profileService.findAllProfiles();
        return ResponseEntity.ok(profileRequestDTOS);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Profile> criarPerfil(@Valid @RequestBody ProfileRequestDTO request) {
        Profile profile = profileService.criarPerfil(request);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<Profile> updateProfile(
            @PathVariable UUID profileId,
            @Valid @RequestBody ProfileRequestDTO request,
            @AuthenticationPrincipal Profile currentUser
    ) {
        if (!currentUser.getId().equals(profileId) && currentUser.getRole() != ProfileRole.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        Profile updatedProfile = profileService.updateProfile(profileId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProfile(@PathVariable UUID profileId) {
        profileService.deleteProfile(profileId);
        return ResponseEntity.noContent().build();
    }
}