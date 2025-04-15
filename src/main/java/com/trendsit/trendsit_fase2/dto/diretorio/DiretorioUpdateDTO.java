package com.trendsit.trendsit_fase2.dto.diretorio;

import java.util.List;
import java.util.UUID;

public record DiretorioUpdateDTO(
        String novaTurma,
        List<UUID> novosAlunosIds,
        UUID novoProfessorId
) {}