package com.trendsit.trendsit_fase2.service.vaga;

import com.trendsit.trendsit_fase2.dto.vaga.VagaDTO;
import com.trendsit.trendsit_fase2.dto.vaga.VagaResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.vaga.VagaResponseDTO;
import com.trendsit.trendsit_fase2.model.evento.Evento;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.model.vaga.Vaga;
import com.trendsit.trendsit_fase2.repository.evento.EventoRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.repository.vaga.VagaRepository;
import com.trendsit.trendsit_fase2.service.auth.AuthorizationService;
import com.trendsit.trendsit_fase2.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class VagaService {
    private final VagaRepository vagaRepository;
    private final ProfileRepository profileRepository;
    private final AuthorizationService authorizationService;
    private final EventoRepository eventoRepository;
    private final NotificationService notificationService;

    public VagaService(
            VagaRepository vagaRepository,
            ProfileRepository profileRepository,
            AuthorizationService authorizationService,
            EventoRepository eventoRepository, NotificationService notificationService
    ) {
        this.vagaRepository = vagaRepository;
        this.profileRepository = profileRepository;
        this.authorizationService = authorizationService;
        this.eventoRepository = eventoRepository;
        this.notificationService = notificationService;
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
        vaga.setCodigoVaga(gerarCodigoVagaUnico());

        return vagaRepository.save(vaga);
    }

    // Atualizar vaga (autor ou admin)
    public Vaga updateVaga(Long codigoVaga, VagaDTO dto, UUID currentUserId) {
        // 1. Find by codigoVaga instead of ID
        Vaga vaga = vagaRepository.findByCodigoVaga(codigoVaga)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));

        // 2. Authorization check
        if (!authorizationService.isOwnerOrAdmin(vaga, currentUserId)) {
            throw new AccessDeniedException("Acesso negado");
        }

        // 3. Update fields
        vaga.setTitulo(dto.getTitulo());
        vaga.setConteudo(dto.getConteudo());
        vaga.setSalario(dto.getSalario());
        vaga.setLocal(dto.getLocal());

        return vagaRepository.save(vaga);
    }

    // Moderar vaga (apenas admin)
    @Transactional
    public void deleteVagaByCodigo(Long codigoVaga, UUID adminId) {
        Vaga vaga = vagaRepository.findByCodigoVaga(codigoVaga)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));

        Profile admin = profileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin não encontrado"));

        if (admin.getRole() != ProfileRole.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem excluir vagas");
        }

        // Exclui todas as notificações associadas à vaga
        notificationService.deleteNotificationsByVaga(vaga); // Nova linha

        vagaRepository.delete(vaga);
    }



    private Long gerarCodigoVagaUnico() {
        Long codigo;
        int maxAttempts = 10;
        int attempts = 0;

        do {
            codigo = ThreadLocalRandom.current().nextLong(1L, 10_000_000_000L); // 10-digit number
            attempts++;
        } while (vagaRepository.existsByCodigoVaga(codigo) && attempts < maxAttempts);

        if (attempts >= maxAttempts) {
            throw new RuntimeException("Não foi possível gerar um código único após 10 tentativas");
        }

        return codigo;
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


    public Evento updateEventStatus(Long codigoEvento, Evento.Status status) {
        Evento evento = (Evento) eventoRepository.findByCodigoEvento(codigoEvento)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));

        // Only allow status changes (codigoEvento remains the same)
        evento.setStatus(status);

        return eventoRepository.save(evento);
    }

    // No VagaService.java
    public Vaga moderateVaga(Long id, Vaga.Status status, UUID adminId, String rejectionReason) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));

        Profile admin = profileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin não encontrado"));

        if (admin.getRole() != ProfileRole.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem moderar vagas");
        }

        vaga.setStatus(status);
        Vaga updatedVaga = vagaRepository.save(vaga);

        if (status == Vaga.Status.REJEITADO || status == Vaga.Status.APROVADO) {
            notificationService.createVacancyNotification(vaga, status, rejectionReason);
        }

        return updatedVaga;
    }
}