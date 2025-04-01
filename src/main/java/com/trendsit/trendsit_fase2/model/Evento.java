package com.trendsit.trendsit_fase2.model;

import com.trendsit.trendsit_fase2.util.OwnableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "eventos")
@NoArgsConstructor
public class Evento implements OwnableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String titulo;

    @Column(nullable = false)
    private String conteudo;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Profile autor;

    @Column
    private Boolean status;
}
