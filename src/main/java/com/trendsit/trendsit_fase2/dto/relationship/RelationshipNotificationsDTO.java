package com.trendsit.trendsit_fase2.dto.relationship;

import com.trendsit.trendsit_fase2.dto.evento.EventNotificationDTO;
import com.trendsit.trendsit_fase2.dto.group.GroupInviteNotificationDTO;
import com.trendsit.trendsit_fase2.dto.notification.VacancyNotificationDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipNotificationsDTO {
    private List<FriendRequestDTO> receivedRequests;
    private List<FriendRequestDTO> sentRequests;
    private List<FollowerDTO> followers;
    private List<VacancyNotificationDTO> vacancyNotifications;
    private List<EventNotificationDTO> eventNotifications;
    private List<GroupInviteNotificationDTO> groupInviteNotifications;
}