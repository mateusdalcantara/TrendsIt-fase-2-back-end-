package com.trendsit.trendsit_fase2.dto.relationship;

import com.trendsit.trendsit_fase2.dto.evento.EventNotificationDTO;
import com.trendsit.trendsit_fase2.dto.notification.VacancyNotificationDTO;
import lombok.Getter;
import java.util.List;

@Getter
public class RelationshipNotificationsDTO {
    private List<FriendRequestDTO> receivedRequests;
    private List<FriendRequestDTO> sentRequests;
    private List<FollowerDTO> followers;
    private List<VacancyNotificationDTO> vacancyNotifications; // Campo unificado
    private List<EventNotificationDTO> eventNotifications;

    // Construtor com todos os campos
    public RelationshipNotificationsDTO(
            List<FriendRequestDTO> receivedRequests,
            List<FriendRequestDTO> sentRequests,
            List<FollowerDTO> followers,
            List<VacancyNotificationDTO> vacancyNotifications,
            List<EventNotificationDTO> eventNotifications
    ) {
        this.receivedRequests = receivedRequests;
        this.sentRequests = sentRequests;
        this.followers = followers;
        this.vacancyNotifications = vacancyNotifications;
        this.eventNotifications = eventNotifications;

    }

    // Getters
}
