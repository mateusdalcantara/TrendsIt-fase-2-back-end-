package com.trendsit.trendsit_fase2.model.friendship;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.repository.relationship.FriendshipRepository;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "friendships")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public enum Status {
        PENDING,    // Request sent but not yet accepted
        ACCEPTED,   // Request accepted
        DECLINED,  // Request declined
        DELETED
    }

    /**
     * Usuario que enviou a requisicao de amizade
     * Mapeamento para 'user_from_id' column no banco de dados
     * Exemplo: Profile(id=123, username="Gabriel")
     *
     * @see FriendshipRepository
     * @see Profile
     */
    @ManyToOne
    @JoinColumn(name = "user_from_id")
    private Profile userFrom;

    /**
     * Usuario que vai receber a requisicao de amizade
     * Mapeamento para 'user_to_id' column no banco de dados
     * Exemplo: Profile(id=456, username="Mateus")
     *
     * @see FriendshipRepository
     * @see Profile
     */
    @ManyToOne
    @JoinColumn(name = "user_to_id")
    private Profile userTo;

    /**
     * Current status of the friendship request
     * Defaults to PENDING when creating new requests
     * Stored as string in database (e.g., "PENDING")
     */
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    /**
     * Automatic timestamp for when the request was created
     * Managed by Hibernate automatically
     * Example: 2025-04-20T14:30:00
     */
    @CreationTimestamp
    private LocalDateTime createdAt;
}
