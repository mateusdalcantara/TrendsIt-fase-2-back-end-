package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.Evento.EventoDTO;
import com.trendsit.trendsit_fase2.dto.Evento.EventoResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.Evento.EventoResponseDTO;
import com.trendsit.trendsit_fase2.model.Evento;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.model.ProfileRole;
import com.trendsit.trendsit_fase2.repository.EventoRepository;
import com.trendsit.trendsit_fase2.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
        evento.setTitulo(dto.getTitulo());
        evento.setConteudo(dto.getConteudo());
        evento.setDataEvento(dto.getDataEvento());
        evento.setLocal(dto.getLocal());
        evento.setAutor(autor);
        evento.setStatus(Evento.Status.PENDENTE);

        return eventoRepository.save(evento);
    }



    public Evento updateEvent(Long id, EventoDTO dto, UUID currentUserId) throws AccessDeniedException {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));

        if (!authorizationService.isOwnerOrAdmin(evento, currentUserId)) {
            throw new AccessDeniedException("Acesso negado");
        }

        evento.setTitulo(dto.getTitulo());
        evento.setConteudo(dto.getConteudo());
        evento.setDataEvento(dto.getDataEvento());
        evento.setLocal(dto.getLocal());

        return eventoRepository.save(evento);
    }

    public Evento updateEventStatus(Long id, Evento.Status status, UUID userId) throws AccessDeniedException {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));

        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        if (!user.getRole().equals(ProfileRole.ADMIN)) {
            throw new AccessDeniedException("Apenas administradores podem atualizar o status do evento.");
        }

        evento.setStatus(status);
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

}
