package com.trendsit.trendsit_fase2.dto.relationship;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateFriendNumberRequest {
    @NotNull(message = "O número amigo é obrigatório")
    @Min(value = 1L, message = "Mínimo de 1 dígito")
    @Max(value = 999_999_999_999L, message = "Máximo de 12 dígitos")
    private Long newFriendNumber;

    public Long getNewFriendNumber() {
        return newFriendNumber;
    }

    public void setNewFriendNumber(Long newFriendNumber) {
        this.newFriendNumber = newFriendNumber;
    }
}