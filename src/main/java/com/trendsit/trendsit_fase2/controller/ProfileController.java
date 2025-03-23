package com.trendsit.trendsit_fase2.controller;

import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.model.ProfileRole;
import com.trendsit.trendsit_fase2.service.ProfileService;
import com.trendsit.trendsit_fase2.dto.ProfileRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Profile> criarPerfil(@Valid @RequestBody ProfileRequest request) {
        Profile profile = profileService.criarPerfil(request);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<Profile> updateProfile(
            @PathVariable UUID profileId,
            @Valid @RequestBody ProfileRequest request,
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