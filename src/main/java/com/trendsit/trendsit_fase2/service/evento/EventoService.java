package com.trendsit.trendsit_fase2.service.evento;

import com.trendsit.trendsit_fase2.dto.evento.EventoDTO;
import com.trendsit.trendsit_fase2.dto.evento.EventoResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.evento.EventoResponseDTO;
import com.trendsit.trendsit_fase2.model.evento.Evento;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.evento.EventoRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.auth.AuthorizationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EventoService {
    AuthorizationService authorizationService;
    EventoRepository eventoRepository;
    ProfileRepository profileRepository;
    public EventoService(EventoRepository eventoRepository, ProfileRepository profileRepository, AuthorizationService authorizationService){
        this.eventoRepository = eventoRepository;
        this.profileRepository = profileRepository;
        this.authorizationService = authorizationService;

    }

    public List<EventoResponseDTO> findAllEvents() {
        return eventoRepository.findAllApprovedEvents().stream()
                .map(EventoResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Evento createEvent(EventoDTO dto, UUID autorId) {
        Profile autor = profileRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        Evento evento = new Evento();
        evento.setCodigoEvento(gerarCodigoEventoUnico()); // Generate on creation
        evento.setTitulo(dto.getTitulo());
        evento.setConteudo(dto.getConteudo());
        evento.setDataEvento(dto.getDataEvento());
        evento.setLocal(dto.getLocal());
        evento.setAutor(autor);
        evento.setStatus(Evento.Status.PENDENTE); // Default status

        return eventoRepository.save(evento);
    }

    public Evento updateEvent(Long codigoEvento, EventoDTO dto, UUID currentUserId)
            throws AccessDeniedException {

        Evento evento = (Evento) eventoRepository.findByCodigoEvento(codigoEvento)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));

        // Authorization logic
        if (!evento.getAutor().getId().equals(currentUserId) &&
                !profileRepository.findById(currentUserId)
                        .map(p -> p.getRole() == ProfileRole.ADMIN)
                        .orElse(false)) {
            throw new AccessDeniedException("Acesso negado");
        }

        // Update fields
        evento.setTitulo(dto.getTitulo());
        evento.setConteudo(dto.getConteudo());
        evento.setDataEvento(dto.getDataEvento());
        evento.setLocal(dto.getLocal());

        return eventoRepository.save(evento);
    }



    public List<EventoResponseAdminDTO> findAllEventsAdmin() {
        return eventoRepository.findAll().stream()
                .map(EventoResponseAdminDTO::new)
                .collect(Collectors.toList());
    }

    public List<EventoResponseAdminDTO> findAllPendingEvents() {
        return eventoRepository.findAllPendingEvents().stream()
                .map(EventoResponseAdminDTO::new)
                .collect(Collectors.toList());
    }

    private Long gerarCodigoEventoUnico() {
        Long codigo;
        int maxAttempts = 10;
        int attempts = 0;

        do {
            codigo = ThreadLocalRandom.current().nextLong(1L, 10_000_000_000L);
            attempts++;
        } while (eventoRepository.existsByCodigoEvento(codigo) && attempts < maxAttempts);

        if (attempts >= maxAttempts) {
            throw new RuntimeException("Failed to generate unique event code");
        }

        return codigo;
    }

    public Evento updateEventStatus(Long codigoEvento, Evento.Status status, UUID adminId) throws AccessDeniedException {
        // 1. Find event by codigoEvento
        Evento evento = (Evento) eventoRepository.findByCodigoEvento(codigoEvento)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));

        // 2. Verify admin privileges
        Profile admin = profileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin não encontrado"));

        if (!admin.getRole().equals(ProfileRole.ADMIN)) {
            throw new AccessDeniedException("Apenas administradores podem modificar status");
        }

        // 3. Update status
        evento.setStatus(status);
        return eventoRepository.save(evento);
    }

    @Transactional
    public void deleteEvento(Long codigoEvento, UUID adminId) throws AccessDeniedException {
        Profile admin = profileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!admin.getRole().equals(ProfileRole.ADMIN)) {
            throw new AccessDeniedException("Apenas administradores podem excluir eventos");
        }

        Evento evento = eventoRepository.findByCodigoEvento(codigoEvento)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));

        eventoRepository.delete(evento);
    }
}
