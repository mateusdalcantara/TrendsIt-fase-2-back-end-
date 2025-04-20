package com.trendsit.trendsit_fase2.service.group;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "group_post_comments")
@Getter
@Setter
public class GroupPostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference
    private GroupPost post;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Profile author;

    private LocalDateTime createdAt = LocalDateTime.now();
}
