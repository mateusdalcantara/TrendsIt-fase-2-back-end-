package com.trendsit.trendsit_fase2.model.notification;

import com.trendsit.trendsit_fase2.model.evento.Evento;
import com.trendsit.trendsit_fase2.model.group.Group;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.vaga.Vaga;
import com.trendsit.trendsit_fase2.service.group.GroupInvitation;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private Profile recipient;


    private String type; // Ex: VACANCY_REJECTED
    private String message;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
            name = "invitation_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_notification_invitation")
    )
    private GroupInvitation invitation;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
            name = "vacancy_id",             // a coluna na notification
            referencedColumnName = "id",     // a PK da Vaga
            foreignKey = @ForeignKey(name = "FK_notification_vaga")
    )
    private Vaga vaga;  // Relação com Vaga de trabalho

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private Evento evento; // Relação com Evento

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
            name = "group_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_notification_group")
    )
    private Group group;

    private boolean isRead = false;
}