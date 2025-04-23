package com.trendsit.trendsit_fase2.dto.diretorio;

import com.trendsit.trendsit_fase2.dto.profile.ProfilePublicoDTO;
import com.trendsit.trendsit_fase2.model.diretorio.Diretorio;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DiretorioDTO {
    private long id;
    private String turma;
    private ProfilePublicoDTO professor;
    private List<ProfilePublicoDTO> alunos;

    public DiretorioDTO(Diretorio diretorio) {
        this.id = diretorio.getId();
        this.turma = diretorio.getTurma();

        if(diretorio.getPrimaryProfessor() != null) {
            this.professor = new ProfilePublicoDTO(diretorio.getPrimaryProfessor());
        }

        this.alunos = diretorio.getAlunos().stream()
                .map(ProfilePublicoDTO::new)
                .collect(Collectors.toList());
    }
}