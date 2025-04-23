package com.trendsit.trendsit_fase2.service.notification;

import com.trendsit.trendsit_fase2.model.evento.Evento;
import com.trendsit.trendsit_fase2.model.notification.Notification;
import com.trendsit.trendsit_fase2.model.vaga.Vaga;
import com.trendsit.trendsit_fase2.repository.evento.EventoRepository;
import com.trendsit.trendsit_fase2.repository.notification.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EventoRepository eventoRepository;

    public NotificationService(NotificationRepository notificationRepository, EventoRepository eventoRepository) {
        this.notificationRepository = notificationRepository;
        this.eventoRepository = eventoRepository;
    }

    public void deleteNotificationsByEvento(Evento evento) {
        notificationRepository.deleteByEvento(evento);
    }

    // Método específico para criar notificações de vaga
    public void createVacancyNotification(Vaga vaga, Vaga.Status status, String rejectionReason) {
        Notification notification = new Notification();
        notification.setRecipient(vaga.getAutor());
        notification.setType(status == Vaga.Status.APROVADO
                ? "VACANCY_APPROVED"
                : "VACANCY_REJECTED");
        notification.setMessage(status == Vaga.Status.APROVADO
                ? "Sua vaga '" + vaga.getTitulo() + "' foi aprovada e está publicada!"
                : "Sua vaga '" + vaga.getTitulo() + "' foi rejeitada. Motivo: " + rejectionReason);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setVaga(vaga);

        notificationRepository.save(notification);
    }

    public void createEventNotification(Evento evento, Evento.Status status, String rejectionReason) {
        Notification notification = new Notification();
        notification.setRecipient(evento.getAutor());
        notification.setType(status == Evento.Status.APROVADO ? "EVENT_APPROVED" : "EVENT_REJECTED");
        notification.setMessage(status == Evento.Status.APROVADO
                ? "Seu evento '" + evento.getTitulo() + "' foi aprovado e está público!"
                : "Seu evento '" + evento.getTitulo() + "' foi rejeitado. Motivo: " + rejectionReason);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setEvento(evento);
        notification.setVaga(null); // Define explicitamente como null

        notificationRepository.save(notification);
    }
}