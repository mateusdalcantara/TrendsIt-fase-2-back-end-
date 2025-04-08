package com.trendsit.trendsit_fase2.controller.vaga;

import com.trendsit.trendsit_fase2.dto.vaga.VagaDTO;
import com.trendsit.trendsit_fase2.dto.vaga.VagaResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.vaga.VagaResponseDTO;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.vaga.Vaga;
import com.trendsit.trendsit_fase2.service.vaga.VagaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/vagas")
public class VagaController {
    private final VagaService vagaService;

    public VagaController(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    // Listar vagas aprovadas
    @GetMapping
    public ResponseEntity<List<VagaResponseDTO>> listarAprovadas() {
        return ResponseEntity.ok(vagaService.findAllApproved());
    }

    // Criar vaga
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<VagaResponseDTO> criarVaga(
            @Valid @RequestBody VagaDTO dto,
            @AuthenticationPrincipal Profile profile
    ) {
        return ResponseEntity.ok(
                new VagaResponseDTO(vagaService.createVaga(dto, profile.getId()))
        );
    }

    // Atualizar vaga
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<VagaResponseDTO> atualizarVaga(
            @PathVariable Long id,
            @Valid @RequestBody VagaDTO dto,
            @AuthenticationPrincipal Profile profile
    ) {
        return ResponseEntity.ok(
                new VagaResponseDTO(vagaService.updateVaga(id, dto, profile.getId()))
        );
    }

    // Moderar vaga (admin)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VagaResponseAdminDTO> moderarVaga(
            @PathVariable Long id,
            @RequestParam Vaga.Status status,
            @AuthenticationPrincipal Profile admin
    ) {
        return ResponseEntity.ok(
                new VagaResponseAdminDTO(vagaService.moderateVaga(id, status, admin.getId()))
        );
    }

    // Listar todas as vagas (admin)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VagaResponseAdminDTO>> listarTodasAdmin() {
        return ResponseEntity.ok(vagaService.findAllAdmin());
    }

    // Listar vagas pendentes (admin)
    @GetMapping("/admin/pendentes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VagaResponseAdminDTO>> listarPendentes() {
        return ResponseEntity.ok(vagaService.findAllPending());
    }
}