package com.trendsit.trendsit_fase2.repository.notification;

import com.trendsit.trendsit_fase2.model.evento.Evento;
import com.trendsit.trendsit_fase2.model.group.Group;
import com.trendsit.trendsit_fase2.model.notification.Notification;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.vaga.Vaga;
import com.trendsit.trendsit_fase2.service.group.GroupInvitation;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(Profile recipient);
    List<Notification> findByRecipientAndTypeOrderByCreatedAtDesc(Profile recipient, String type);
    void deleteByEvento(Evento evento);
    void deleteByVaga(Vaga vaga);
    //void deleteByRecipient(Profile recipient);
    void deleteByGroup(Group group);
    List<Notification> findByRecipientAndTypeIn(Profile recipient, List<String> types);
    List<Notification> findByRecipientAndType(Profile currentUser, String vacancyRejected);
    void deleteByInvitation(GroupInvitation invitation);
    void deleteByGroupAndRecipientAndType(Group group, Profile invited, String groupInvite);
    void deleteByRecipient(Profile recipient);
    void deleteByInvitationInvited(Profile invited);
    void deleteByGroupCriador(Profile criador);



}