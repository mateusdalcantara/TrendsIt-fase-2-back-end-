package com.trendsit.trendsit_fase2.model.evento;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.util.OwnableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "eventos")
public class Evento implements OwnableEntity {
    public enum Status {
        PENDENTE, APROVADO, REJEITADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "codigo_evento", unique = true, nullable = false)
    private Long codigoEvento;

    private String titulo;
    private String conteudo;
    private LocalDateTime dataEvento;
    private String local;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    private Profile autor;
}