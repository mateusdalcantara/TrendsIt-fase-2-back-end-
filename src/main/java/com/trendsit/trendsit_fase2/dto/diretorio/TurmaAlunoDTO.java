package com.trendsit.trendsit_fase2.dto.diretorio;

import com.trendsit.trendsit_fase2.dto.diretorio.AlunoDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TurmaAlunoDTO {
    private String turma;
    private List<AlunoDTO> colegas;

    public TurmaAlunoDTO(String turma, List<AlunoDTO> colegas) {
        this.turma = turma;
        this.colegas = colegas;
    }
}