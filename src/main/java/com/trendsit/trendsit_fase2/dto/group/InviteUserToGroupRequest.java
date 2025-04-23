package com.trendsit.trendsit_fase2.dto.group;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@Setter
public class InviteUserToGroupRequest {
    @NotNull("É necessário informar o usuário a convidar")
    private UUID invitedUserId;
}

