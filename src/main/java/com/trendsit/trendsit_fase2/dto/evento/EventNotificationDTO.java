package com.trendsit.trendsit_fase2.dto.evento;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class EventNotificationDTO {
    private String type; // "APPROVED" ou "REJECTED"
    private String message;
    private LocalDateTime createdAt;
    private Long eventoId;

    public EventNotificationDTO(String type, String message, LocalDateTime createdAt, Long eventoId) {
        this.type = type;
        this.message = message;
        this.createdAt = createdAt;
        this.eventoId = eventoId;
    }
}