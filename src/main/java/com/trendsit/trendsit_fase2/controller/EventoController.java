package com.trendsit.trendsit_fase2.controller;

import com.trendsit.trendsit_fase2.dto.EventoDTO;
import com.trendsit.trendsit_fase2.dto.EventoResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.EventoResponseDTO;
import com.trendsit.trendsit_fase2.model.Evento;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.service.EventoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/events")
public class EventoController {
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> obterEvento()
    {
        List<EventoResponseDTO> eventos = eventoService.findAllEvents();
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<EventoResponseAdminDTO>> obterEventoAdmin()
    {
        List<EventoResponseAdminDTO> eventos = eventoService.findAllEventsAdmin();
        return ResponseEntity.ok(eventos);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EventoResponseDTO> createEvento(@Valid @RequestBody EventoDTO eventoDto, @AuthenticationPrincipal Profile profile)
    {
        Evento evento = eventoService.createEvent(eventoDto,profile.getId());
        return ResponseEntity.ok(new EventoResponseDTO(evento));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponseDTO> updateEventStatus(
            @PathVariable Long id,
            @RequestParam boolean status,
            @AuthenticationPrincipal Profile user) throws AccessDeniedException {
        Evento updatedEvent = eventoService.updateEventStatus(id, status, user.getId());
        return ResponseEntity.ok(new EventoResponseDTO(updatedEvent));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EventoResponseDTO> updateEvent
            (@PathVariable Long id,
             @Valid @RequestBody EventoDTO eventoDTO,
             @AuthenticationPrincipal Profile user
            ) throws AccessDeniedException {
        Evento updatedEvent = eventoService.updateEvent(id,eventoDTO,user.getId());
        return ResponseEntity.ok(new EventoResponseDTO(updatedEvent));

    }
 }
