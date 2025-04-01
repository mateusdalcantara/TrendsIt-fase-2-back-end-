package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.EventoDTO;
import com.trendsit.trendsit_fase2.dto.EventoResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.EventoResponseDTO;
import com.trendsit.trendsit_fase2.model.Evento;
import com.trendsit.trendsit_fase2.model.Postagem;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.model.ProfileRole;
import com.trendsit.trendsit_fase2.repository.EventoRepository;
import com.trendsit.trendsit_fase2.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
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

    public List<EventoResponseDTO> findAllEvents(){
        List<EventoResponseDTO> evento = eventoRepository.findAllByStatusTrue().stream()
                .map(EventoResponseDTO::new).collect(Collectors.toList());
        return evento;
    }

    public Evento createEvent(EventoDTO eventodto, UUID autorid)
    {
        Profile autor = profileRepository.findById(autorid).orElseThrow(
                () -> new EntityNotFoundException("Perfil não encontrado"));

        Evento eventocreate = new Evento();
        eventocreate.setAutor(autor);
        eventocreate.setTitulo(eventodto.getTitulo());
        eventocreate.setConteudo(eventodto.getConteudo());
        eventocreate.setCreatedAt(LocalDateTime.now());
        eventocreate.setStatus(false);
        return eventoRepository.save(eventocreate);
        
    }

    public Evento updateEvent(Long id, EventoDTO eventoDTO, UUID currentUserId) throws AccessDeniedException {
        Evento evento = eventoRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Evento não encontrado"));
        if (!authorizationService.isOwnerOrAdmin(evento,currentUserId)) {
            throw new AccessDeniedException("Acesso negado");
        }
        evento.setTitulo(eventoDTO.getTitulo());
        evento.setConteudo(eventoDTO.getConteudo());

        return eventoRepository.save(evento);

    }

    public Evento updateEventStatus(Long id, boolean status, UUID userId) throws AccessDeniedException {
        Evento evento = eventoRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Evento não encontrado com id: " + id));

        Profile user = profileRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Perfil não encontrado"));

        if (!user.getRole().equals(ProfileRole.ADMIN)) {
            throw new AccessDeniedException("Apenas administradores podem atualizar o status do evento.");
        }

        evento.setStatus(status);
        return eventoRepository.save(evento);
    }

    public List<EventoResponseAdminDTO> findAllEventsAdmin() {
        return eventoRepository.findAll().stream()
                .map(EventoResponseAdminDTO::new).collect(Collectors.toList());
    }
}
