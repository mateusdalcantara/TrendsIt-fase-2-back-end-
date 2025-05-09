package com.trendsit.trendsit_fase2.controller.evento;

import com.trendsit.trendsit_fase2.dto.evento.EventoDTO;
import com.trendsit.trendsit_fase2.dto.evento.EventoResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.evento.EventoResponseDTO;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.model.evento.Evento;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.evento.EventoRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.evento.EventoService;
import com.trendsit.trendsit_fase2.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/events")
public class EventoController {
    private final EventoService eventoService;
    private final EventoRepository eventoRepository;
    private final ProfileRepository profileRepository;
    private final NotificationService notificationService;

    public EventoController(EventoService eventoService,
                            EventoRepository eventoRepository,
                            ProfileRepository profileRepository, NotificationService notificationService) {

        this.eventoService = eventoService;
        this.eventoRepository = eventoRepository;
        this.profileRepository = profileRepository;
        this.notificationService = notificationService;
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
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO', 'ADMIN')")
    public ResponseEntity<EventoResponseDTO> criarEvento(
            @Valid @RequestBody EventoDTO eventoDto,
            @AuthenticationPrincipal Profile profile) {
        Evento evento = eventoService.createEvent(eventoDto, profile.getId());
        return ResponseEntity.ok(new EventoResponseDTO(evento));
    }

    @PatchMapping("/{codigoEvento}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponseDTO> atualizarStatusEvento(
            @PathVariable Long codigoEvento,
            @RequestParam Evento.Status status,
            @RequestParam(required = false) String rejectionReason, // Novo parâmetro
            @AuthenticationPrincipal Profile admin) throws AccessDeniedException {

        Evento updatedEvent = eventoService.updateEventStatus(
                codigoEvento,
                status,
                admin.getId(),
                rejectionReason // Passa o motivo
        );

        return ResponseEntity.ok(new EventoResponseDTO(updatedEvent));
    }


    @PutMapping("/{codigoEvento}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO', 'ADMIN')")
    public ResponseEntity<EventoResponseDTO> atualizarEvento(
            @PathVariable Long codigoEvento,
            @Valid @RequestBody EventoDTO eventoDTO,
            @AuthenticationPrincipal Profile user) throws AccessDeniedException {

        Evento updatedEvent = eventoService.updateEvent(codigoEvento, eventoDTO, user.getId());
        return ResponseEntity.ok(new EventoResponseDTO(updatedEvent));
    }

    @DeleteMapping("/{codigoEvento}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvento(
            @PathVariable Long codigoEvento,
            @AuthenticationPrincipal Profile currentUser) throws AccessDeniedException {

        eventoService.deleteEvento(codigoEvento, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

 }
