package com.trendsit.trendsit_fase2.controller.diretorio;

import com.trendsit.trendsit_fase2.dto.diretorio.*;
import com.trendsit.trendsit_fase2.dto.profile.ProfileResponseDTO;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.model.diretorio.Diretorio;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.diretorio.DiretorioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/diretorio")
@RequiredArgsConstructor
public class DiretorioController {
    private final DiretorioServiceImpl diretorioService;
    private final ProfileRepository profileRepository;


    @GetMapping("/obterdiretorio")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    public ResponseEntity<List<DiretorioDTO>> findAllDiretorio() {
        return ResponseEntity.ok(diretorioService.findAllDiretorio());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Diretorio> criarDiretorio(@RequestBody DiretorioRequestDTO request) {
        Diretorio novoDiretorio = diretorioService.criarDiretorio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoDiretorio);
    }

    @GetMapping("/aluno/meu-diretorio")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<TurmaAlunoDTO> listarMeusColegas(
            @AuthenticationPrincipal Profile profile
    ) {
        TurmaAlunoDTO dto = diretorioService.listarMeusColegas(profile.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/setteacher/{diretorioid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setProfessor(
            @PathVariable Long diretorioid,
            @RequestParam UUID professorId) {

        try {
            Diretorio diretorio = diretorioService.addProfessor(professorId, diretorioid);
            return ResponseEntity.ok(new ProfileResponseDTO(diretorio.getPrimaryProfessor()));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/removeteacher/{diretorioid}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> removeProfessor(@PathVariable Long diretorioid) {
        try {
            Diretorio diretorio = diretorioService.removeProfessor(diretorioid);
            return ResponseEntity.ok().body("Professor removido com sucesso");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/{turmaId}/alunos")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> addAluno(
            @PathVariable Long turmaId,
            @RequestParam UUID alunoId) {

        diretorioService.addAluno(alunoId, turmaId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDiretorio(@PathVariable Long id) {
        try {
            diretorioService.deleteDiretorio(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDiretorio(
            @PathVariable Long id,
            @RequestBody DiretorioUpdateDTO updateDTO
    ) {
        try {
            DiretorioDTO updated = diretorioService.updateDiretorio(id, updateDTO);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{diretorioId}/alunos/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> removeAluno(
            @PathVariable Long diretorioId,
            @PathVariable UUID alunoId
    ) {
        diretorioService.removeAlunoFromDiretorio(alunoId, diretorioId);
        return ResponseEntity.noContent().build();
    }


}
