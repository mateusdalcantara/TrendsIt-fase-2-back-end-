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
@NoArgsConstructor
@Entity
@Table(name = "vagas")
public class Vaga implements OwnableEntity {
    public enum Status {
        PENDENTE, APROVADO, REJEITADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String conteudo;
    private String salario;
    private String local;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    private Profile autor;
}