package com.trendsit.trendsit_fase2.service.group;

import com.trendsit.trendsit_fase2.model.group.Group;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.util.UUID;

@Entity
@Table(name = "group_invitation")
public class GroupInvitation {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(columnDefinition = "UUID", updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "invited_id", nullable = false)
    private Profile invited;

    @ManyToOne
    @JoinColumn(name = "invited_user_id")
    private Profile invitedUser;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING,
        ACCEPTED,
        DECLINED
    }

    // Getters e setters omitidos para brevidade


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Profile getInvited() {
        return invited;
    }

    public void setInvited(Profile invited) {
        this.invited = invited;
    }

    public Profile getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(Profile invitedUser) {
        this.invitedUser = invitedUser;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

