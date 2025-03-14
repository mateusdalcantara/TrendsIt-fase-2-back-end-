package com.trendsit.trendsit_fase2.controller;

import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.service.ProfileService;
import com.trendsit.trendsit_fase2.dto.ProfileRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Profile> criarPerfil(@RequestBody ProfileRequest request) {
        Profile profile = profileService.criarPerfil(request.getIdade(), request.getCurso());
        return ResponseEntity.ok(profile);
    }
}