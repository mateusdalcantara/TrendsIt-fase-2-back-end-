package com.trendsit.trendsit_fase2.service.diretorio;

import com.trendsit.trendsit_fase2.dto.diretorio.DiretorioDTO;
import com.trendsit.trendsit_fase2.dto.diretorio.TurmaAlunoDTO;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface DiretorioService {
    List<Profile> getAlunos();
    TurmaAlunoDTO listarMeusColegas(UUID alunoId);

    List<DiretorioDTO> findAllDiretorio();
}
