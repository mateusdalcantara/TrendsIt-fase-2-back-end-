package com.trendsit.trendsit_fase2.dto.notification;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class VacancyNotificationDTO {
    private String type; // "APPROVED" ou "REJECTED"
    private String message;
    private LocalDateTime createdAt;
    private Long vacancyId;

    // Construtor com todos os campos
    public VacancyNotificationDTO(
            String type,
            String message,
            LocalDateTime createdAt,
            Long vacancyId
    ) {
        this.type = type;
        this.message = message;
        this.createdAt = createdAt;
        this.vacancyId = vacancyId;
    }
}