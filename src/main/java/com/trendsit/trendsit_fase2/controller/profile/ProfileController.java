package com.trendsit.trendsit_fase2.controller.profile;

import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.service.profile.ProfileService;
import com.trendsit.trendsit_fase2.dto.profile.*;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.service.postagem.PostagemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controlador para gerenciamento de perfis de usuários.
 * Requer autenticação para acesso.
 */

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final PostagemService postagemService;


    public ProfileController(ProfileService profileService, PostagemService postagemService) {
        this.profileService = profileService;
        this.postagemService = postagemService;
    }

    @GetMapping
    public ResponseEntity<List<ProfilePublicoDTO>> obterPerfil() {
        List<ProfilePublicoDTO> profiles = profileService.findAllPublicoProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/meu-perfil")
    public ResponseEntity<ProfileResponseDTO> getMeuPerfil(
            @AuthenticationPrincipal Profile usuarioAtualPrincipal
    ) {
        // 1) pega o ID do principal
        UUID meuId = usuarioAtualPrincipal.getId();

        // 2) busca no banco a entidade completa
        Profile perfil = profileService.findById(meuId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        // 3) monta o DTO a partir do registro no banco
        ProfileResponseDTO dto = new ProfileResponseDTO(perfil);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/admin/listar-usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProfileAdminDTO>> getAllUsersAdmin() {
        List<ProfileAdminDTO> users = profileService.findAllForAdmin();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/atualizar-meu-perfil")
    @PreAuthorize("hasAnyRole('ALUNO', 'ADMIN')")
    public ResponseEntity<ProfileRequestDTO> atualizarMeuPerfil(
            @Valid @RequestBody ProfileUpdateDTO dto, // Changed DTO type
            @AuthenticationPrincipal Profile usuarioLogado
    ) {
        Profile perfilAtualizado = postagemService.atualizarPerfilUsuario(
                usuarioLogado.getId(),
                dto
        );
        return ResponseEntity.ok(new ProfileRequestDTO(perfilAtualizado));
    }

    @PutMapping("/admin/atualizar-profile-usuario/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileAdminDTO> atualizarPerfilAdmin(
            @PathVariable UUID userId,
            @Valid @RequestBody ProfileAdminUpdateDTO request) {
        Profile updatedProfile = profileService.atualizarPerfilAdmin(userId, request);
        return ResponseEntity.ok(new ProfileAdminDTO(updatedProfile));
    }

    @DeleteMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletarPerfil(@PathVariable UUID userId) {
        profileService.deleteProfile(userId);
        return ResponseEntity.ok("Perfil deletado com sucesso");
    }
}

