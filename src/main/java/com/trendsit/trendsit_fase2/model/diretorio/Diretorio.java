package com.trendsit.trendsit_fase2.model.diretorio;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "diretorio")
public class Diretorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String turma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id") // Nome da coluna FK
    private Profile professor;

    @OneToMany(mappedBy = "diretorio")
    private List<Profile> alunos = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String nome;

    public void addAluno(Profile aluno) {
        if (!this.alunos.contains(aluno)) {
            this.alunos.add(aluno);
            aluno.setDiretorio(this);
        }
    }
    public void clearAlunos() {
        this.alunos.forEach(aluno -> aluno.setDiretorio(null));
        this.alunos.clear();
    }
    public void removeAluno(Profile aluno) {
        if (this.alunos.remove(aluno)) {
            aluno.setDiretorio(null);
        }

    }
}
