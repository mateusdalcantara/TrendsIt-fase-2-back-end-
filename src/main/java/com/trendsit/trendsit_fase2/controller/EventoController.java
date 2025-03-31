package com.trendsit.trendsit_fase2.controller;

import com.trendsit.trendsit_fase2.dto.EventoDTO;
import com.trendsit.trendsit_fase2.dto.EventoResponseDTO;
import com.trendsit.trendsit_fase2.model.Evento;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.service.EventoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EventoDTO> createEvento(@Valid @RequestBody EventoDTO eventoDto, @AuthenticationPrincipal Profile profile)
    {
        Evento evento = eventoService.createEvent(eventoDto,profile.getId());
        return ResponseEntity.ok(eventoDto);
    }
 }
