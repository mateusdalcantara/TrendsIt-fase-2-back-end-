package com.trendsit.trendsit_fase2.controller;

import com.trendsit.trendsit_fase2.dto.Evento.EventoDTO;
import com.trendsit.trendsit_fase2.dto.Evento.EventoResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.Evento.EventoResponseDTO;
import com.trendsit.trendsit_fase2.model.Evento;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.service.EventoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<EventoResponseDTO>> obterEventos() {
        return ResponseEntity.ok(eventoService.findAllEvents());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventoResponseAdminDTO>> obterTodosEventosAdmin() {
        return ResponseEntity.ok(eventoService.findAllEventsAdmin());
    }

    @GetMapping("/admin/pendentes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventoResponseAdminDTO>> obterEventosPendentes() {
        return ResponseEntity.ok(eventoService.findAllPendingEvents());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EventoResponseDTO> criarEvento(
            @Valid @RequestBody EventoDTO eventoDto,
            @AuthenticationPrincipal Profile profile) {
        Evento evento = eventoService.createEvent(eventoDto, profile.getId());
        return ResponseEntity.ok(new EventoResponseDTO(evento));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponseDTO> atualizarStatusEvento(
            @PathVariable Long id,
            @RequestParam Evento.Status status,
            @AuthenticationPrincipal Profile user) throws AccessDeniedException {
        Evento updatedEvent = eventoService.updateEventStatus(id, status, user.getId());
        return ResponseEntity.ok(new EventoResponseDTO(updatedEvent));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EventoResponseDTO> atualizarEvento(
            @PathVariable Long id,
            @Valid @RequestBody EventoDTO eventoDTO,
            @AuthenticationPrincipal Profile user) throws AccessDeniedException {
        Evento updatedEvent = eventoService.updateEvent(id, eventoDTO, user.getId());
        return ResponseEntity.ok(new EventoResponseDTO(updatedEvent));
    }
 }
