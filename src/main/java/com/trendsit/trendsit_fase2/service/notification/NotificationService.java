package com.trendsit.trendsit_fase2.service.notification;

import com.trendsit.trendsit_fase2.model.evento.Evento;
import com.trendsit.trendsit_fase2.model.group.Group;
import com.trendsit.trendsit_fase2.model.notification.Notification;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.vaga.Vaga;
import com.trendsit.trendsit_fase2.repository.evento.EventoRepository;
import com.trendsit.trendsit_fase2.repository.notification.NotificationRepository;
import com.trendsit.trendsit_fase2.service.group.GroupInvitation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void deleteNotificationsByGroup(Group group) {
        notificationRepository.deleteByGroup(group);
    }

    public void deleteNotificationsByRecipient(Profile recipient) {
        notificationRepository.deleteByRecipient(recipient);
    }

    public void deleteNotificationsByVaga(Vaga vaga) {
        notificationRepository.deleteByVaga(vaga);
    }

    @Transactional
    public void deleteGroupInviteNotification(GroupInvitation invitation) {
        // Implementação 100% segura mesmo sem relação direta
        notificationRepository.deleteByGroupAndRecipientAndType(
                invitation.getGroup(),
                invitation.getInvited(),
                "GROUP_INVITE"
        );
    }


    public void createGroupInviteNotification(GroupInvitation invitation) {
        Notification notification = new Notification();
        notification.setRecipient(invitation.getInvited());
        notification.setType("GROUP_INVITE");
        notification.setMessage(
                String.format("Você foi convidado para o grupo '%s' por %s.",
                        invitation.getGroup().getNome(),
                        invitation.getGroup().getCriador().getUsername()
                )
        );
        notification.setCreatedAt(LocalDateTime.now());
        notification.setGroup(invitation.getGroup());
        notification.setInvitation(invitation);
        notificationRepository.save(notification);
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

    @Transactional
    public void deleteAllNotificationsRelatedToProfile(Profile profile) {
        // Notificações onde o profile é recipient
        notificationRepository.deleteByRecipient(profile);
        // Notificações onde o profile é relacionado (ex: convites)
        notificationRepository.deleteByInvitationInvited(profile);
        notificationRepository.deleteByGroupCriador(profile);
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


    @Transactional
    public void deleteAllByProfile(Profile profile) {
        // Deleta notificações onde o perfil é o destinatário
        notificationRepository.deleteByRecipient(profile);

        // Deleta notificações onde o perfil é o convidado em um convite
        notificationRepository.deleteByInvitationInvited(profile);

        // Deleta notificações onde o perfil é o criador de um grupo
        notificationRepository.deleteByGroupCriador(profile);
    }
}