package com.trendsit.trendsit_fase2.controller;

import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.service.ProfileService;
import com.trendsit.trendsit_fase2.dto.ProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gerenciamento de perfis via API REST.
 */
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Construtor para injeção de dependência do serviço de perfis.
     * @param profileService Serviço com a lógica de negócio para perfis.
     */
    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Endpoint para criação/atualização de perfis.
     *
     * @param request DTO com os dados do perfil (idade e curso).
     * @return Perfil salvo com status HTTP 200.
     */
    @PostMapping
    public ResponseEntity<Profile> criarPerfil(@RequestBody ProfileRequest request) {
        Profile profile = profileService.criarPerfil(request.getIdade(), request.getCurso());
        return ResponseEntity.ok(profile); // Retorna 200 OK com o perfil criado
    }
}