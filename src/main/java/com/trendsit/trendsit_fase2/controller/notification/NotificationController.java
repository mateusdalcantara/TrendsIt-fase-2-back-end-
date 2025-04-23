package com.trendsit.trendsit_fase2.controller.notification;

import com.trendsit.trendsit_fase2.dto.evento.EventNotificationDTO;
import com.trendsit.trendsit_fase2.dto.notification.VacancyNotificationDTO;
import com.trendsit.trendsit_fase2.dto.relationship.FollowerDTO;
import com.trendsit.trendsit_fase2.dto.relationship.FriendRequestDTO;
import com.trendsit.trendsit_fase2.dto.relationship.RelationshipNotificationsDTO;
import com.trendsit.trendsit_fase2.model.friendship.Friendship;
import com.trendsit.trendsit_fase2.model.notification.Notification; // Import novo
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.repository.notification.NotificationRepository; // Import novo
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.relationship.FollowService;
import com.trendsit.trendsit_fase2.service.relationship.FriendshipService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final FriendshipService friendshipService;
    private final FollowService followService;
    private final ProfileRepository profileRepository;
    private final NotificationRepository notificationRepository; // Adicionado

    public NotificationController(
            FriendshipService friendshipService,
            FollowService followService,
            ProfileRepository profileRepository,
            NotificationRepository notificationRepository // Adicionado
    ) {
        this.friendshipService = friendshipService;
        this.followService = followService;
        this.profileRepository = profileRepository;
        this.notificationRepository = notificationRepository; // Adicionado
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO', 'ADMIN')")
    public ResponseEntity<RelationshipNotificationsDTO> getRelationshipNotifications(
            @AuthenticationPrincipal Profile currentUser
    ) {
        // Notificações de relacionamento
        List<Friendship> receivedPendingRequests =
                friendshipService.getReceivedPendingRequests(currentUser.getId());

        List<Friendship> sentPendingRequests =
                friendshipService.getSentPendingRequests(currentUser.getId());

        List<FollowerDTO> followerDTOs = followService.getFollowers(currentUser.getId()).stream()
                .map(FollowerDTO::new)
                .toList();

        List<FriendRequestDTO> receivedDTOs = receivedPendingRequests.stream()
                .map(FriendRequestDTO::new)
                .toList();

        List<FriendRequestDTO> sentDTOs = sentPendingRequests.stream()
                .map(FriendRequestDTO::new)
                .toList();

        // Notificações de vagas (aprovadas e rejeitadas)
        List<Notification> vacancyNotifications = notificationRepository.findByRecipientAndTypeIn(
                currentUser,
                List.of("VACANCY_APPROVED", "VACANCY_REJECTED")
        );

        List<VacancyNotificationDTO> vacancyDTOs = vacancyNotifications.stream()
                .map(n -> new VacancyNotificationDTO(
                        n.getType().replace("VACANCY_", ""), // Converte para "APPROVED" ou "REJECTED"
                        n.getMessage(),
                        n.getCreatedAt(),
                        n.getVacancy() != null ? n.getVacancy().getId() : null
                ))
                .toList();

        List<Notification> eventNotifications = notificationRepository.findByRecipientAndTypeIn(
                currentUser,
                List.of("EVENT_APPROVED", "EVENT_REJECTED")
        );

        List<EventNotificationDTO> eventDTOs = eventNotifications.stream()
                .map(n -> new EventNotificationDTO(
                        n.getType().replace("EVENT_", ""), // "APPROVED" ou "REJECTED"
                        n.getMessage(),
                        n.getCreatedAt(),
                        n.getEvento() != null ? n.getEvento().getId() : null
                ))
                .toList();

        return ResponseEntity.ok(
                new RelationshipNotificationsDTO(
                        receivedDTOs,
                        sentDTOs,
                        followerDTOs,
                        vacancyDTOs,  // Notificações de vagas
                        eventDTOs     // Novo: notificações de eventos
                )
        );
    }
}