package com.trendsit.trendsit_fase2.controller.vaga;

import com.trendsit.trendsit_fase2.dto.vaga.VagaDTO;
import com.trendsit.trendsit_fase2.dto.vaga.VagaResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.vaga.VagaResponseDTO;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.vaga.Vaga;
import com.trendsit.trendsit_fase2.repository.vaga.VagaRepository;
import com.trendsit.trendsit_fase2.service.vaga.VagaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/job")
public class VagaController {
    private final VagaService vagaService;
    private final VagaRepository vagaRepository;

    public VagaController(VagaService vagaService, VagaRepository vagaRepository) {
        this.vagaService = vagaService;
        this.vagaRepository = vagaRepository;
    }

    // Listar vagas aprovadas
    @GetMapping
    public ResponseEntity<List<VagaResponseDTO>> listarAprovadas() {
        return ResponseEntity.ok(vagaService.findAllApproved());
    }

    // Criar vaga
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/create-job-opportunity")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<VagaResponseDTO> criarVaga(
            @Valid @RequestBody VagaDTO dto,
            @AuthenticationPrincipal Profile profile
    ) {
        return ResponseEntity.ok(
                new VagaResponseDTO(vagaService.createVaga(dto, profile.getId()))
        );
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/delete-job/{codigoVaga}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVagaByCodigo(
            @PathVariable Long codigoVaga,
            @AuthenticationPrincipal Profile admin
    ) {
        vagaService.deleteVagaByCodigo(codigoVaga, admin.getId());
        return ResponseEntity.noContent().build();
    }

    // Atualizar vaga
    @PutMapping("/change-description-job-opportunity/{codigoVaga}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<VagaResponseDTO> atualizarVaga(
            @PathVariable Long codigoVaga,
            @Valid @RequestBody VagaDTO dto,
            @AuthenticationPrincipal Profile profile) {

        // Get the updated job opportunity from service
        Vaga updatedVaga = vagaService.updateVaga(codigoVaga, dto, profile.getId());

        // Convert entity to DTO and return
        return ResponseEntity.ok(new VagaResponseDTO(updatedVaga));
    }

    // Moderar vaga (admin)
    @PatchMapping("/admin/allow-or-not-job-opportunity/{id}/status")
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
    @GetMapping("/admin/list-job-opportunity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VagaResponseAdminDTO>> listarTodasAdmin() {
        return ResponseEntity.ok(vagaService.findAllAdmin());
    }

    // Listar vagas pendentes (admin)
    @GetMapping("/admin/peding-job-opportunity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VagaResponseAdminDTO>> listarPendentes() {
        return ResponseEntity.ok(vagaService.findAllPending());
    }
}