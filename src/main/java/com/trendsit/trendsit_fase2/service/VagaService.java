package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.*;
import com.trendsit.trendsit_fase2.model.*;
import com.trendsit.trendsit_fase2.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VagaService {
    private final VagaRepository vagaRepository;
    private final ProfileRepository profileRepository;
    private final AuthorizationService authorizationService;

    public VagaService(
            VagaRepository vagaRepository,
            ProfileRepository profileRepository,
            AuthorizationService authorizationService
    ) {
        this.vagaRepository = vagaRepository;
        this.profileRepository = profileRepository;
        this.authorizationService = authorizationService;
    }

    // Listar vagas aprovadas (usuários comuns)
    public List<VagaResponseDTO> findAllApproved() {
        return vagaRepository.findAllByStatus(Vaga.Status.APROVADO).stream()
                .map(VagaResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Criar nova vaga
    public Vaga createVaga(VagaDTO dto, UUID autorId) {
        Profile autor = profileRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        Vaga vaga = new Vaga();
        vaga.setTitulo(dto.getTitulo());
        vaga.setConteudo(dto.getConteudo());
        vaga.setSalario(dto.getSalario());
        vaga.setLocal(dto.getLocal());
        vaga.setAutor(autor);
        vaga.setStatus(Vaga.Status.PENDENTE);

        return vagaRepository.save(vaga);
    }

    // Atualizar vaga (autor ou admin)
    public Vaga updateVaga(Long id, VagaDTO dto, UUID currentUserId) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));

        if (!authorizationService.isOwnerOrAdmin(vaga, currentUserId)) {
            throw new AccessDeniedException("Acesso negado");
        }

        vaga.setTitulo(dto.getTitulo());
        vaga.setConteudo(dto.getConteudo());
        vaga.setSalario(dto.getSalario());
        vaga.setLocal(dto.getLocal());

        return vagaRepository.save(vaga);
    }

    // Moderar vaga (apenas admin)
    public Vaga moderateVaga(Long id, Vaga.Status status, UUID userId) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));

        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        if (!user.getRole().equals(ProfileRole.ADMIN)) {
            throw new AccessDeniedException("Apenas administradores podem moderar vagas");
        }

        vaga.setStatus(status);
        return vagaRepository.save(vaga);
    }

    // Listar todas as vagas (admin)
    public List<VagaResponseAdminDTO> findAllAdmin() {
        return vagaRepository.findAll().stream()
                .map(VagaResponseAdminDTO::new)
                .collect(Collectors.toList());
    }

    // Listar vagas pendentes (admin)
    public List<VagaResponseAdminDTO> findAllPending() {
        return vagaRepository.findAllPending().stream()
                .map(VagaResponseAdminDTO::new)
                .collect(Collectors.toList());
    }
}