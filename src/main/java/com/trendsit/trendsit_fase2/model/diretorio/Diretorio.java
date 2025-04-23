package com.trendsit.trendsit_fase2.model.diretorio;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

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
    @JoinColumn(name = "professor_id")
    private Profile primaryProfessor;

    @OneToMany(mappedBy = "diretorio", fetch = FetchType.LAZY)
    private List<Profile> alunos;

    @Column(name = "titulo_do_curso")
    private String tituloDoCurso;


    public void addAluno(Profile aluno) {
        if (!this.alunos.contains(aluno)) {
            this.alunos.add(aluno);
            aluno.setDiretorio(this);
            aluno.setCurso(this.tituloDoCurso); // Atribui o nome do diretório ao curso do aluno
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
