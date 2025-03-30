package com.trendsit.trendsit_fase2.dto;

import com.trendsit.trendsit_fase2.model.ProfileRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AtualizarRoleDTO(
        @NotNull(message = "ID do usuário é obrigatório")
        UUID userId,
        @NotNull(message = "Novo role é obrigatório")
        ProfileRole novoRole) {
}