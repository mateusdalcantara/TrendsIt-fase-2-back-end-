package com.trendsit.trendsit_fase2.dto.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupInviteNotificationDTO {
    private UUID invitationId;
    private UUID groupId;
    private String groupName;
    private String inviterUsername;
    private LocalDateTime createdAt;


}

