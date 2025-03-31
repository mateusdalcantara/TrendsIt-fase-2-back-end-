package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.EventoDTO;
import com.trendsit.trendsit_fase2.dto.EventoResponseDTO;
import com.trendsit.trendsit_fase2.model.Evento;
import com.trendsit.trendsit_fase2.model.Postagem;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.repository.EventoRepository;
import com.trendsit.trendsit_fase2.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class EventoService {
    EventoRepository eventoRepository;
    ProfileRepository profileRepository;
    public EventoService(EventoRepository eventoRepository, ProfileRepository profileRepository){
        this.eventoRepository = eventoRepository;
        this.profileRepository = profileRepository;
    }

    public List<EventoResponseDTO> findAllEvents(){
        return eventoRepository.findAll().stream()
                .map(EventoResponseDTO::new).collect(Collectors.toList());
    }

    public Evento createEvent(EventoDTO eventodto, UUID autorid)
    {
        Profile autor = profileRepository.findById(autorid).orElseThrow(
                () -> new EntityNotFoundException("Perfil não encontrado"));

        Evento evento = new Evento();
        evento.setAutor(autor);
        evento.setTitulo(eventodto.getTitulo());
        evento.setConteudo(eventodto.getConteudo());
        evento.setCreatedAt(LocalDateTime.now());
        evento.setStatus(false);
        return eventoRepository.save(evento);
        
    }







}
