package com.trendsit.trendsit_fase2.dto.group;

import java.util.UUID;

public class InviteUserToGroupRequest {
    private UUID invitedUserId;

    public UUID getInvitedUserId() {
        return invitedUserId;
    }

    public void setInvitedUserId(UUID invitedUserId) {
        this.invitedUserId = invitedUserId;
    }
}

