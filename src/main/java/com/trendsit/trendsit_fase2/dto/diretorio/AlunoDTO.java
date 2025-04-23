package com.trendsit.trendsit_fase2.dto.diretorio;


import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlunoDTO {
    private String username;
    private String curso;
    private Long friendNumber;
    private LocalDateTime createdAt;

    // Construtor baseado em Profile
    public AlunoDTO(Profile aluno) {
        this.username = aluno.getUsername();
        this.curso = aluno.getCurso();
        this.friendNumber = aluno.getFriendNumber();
        this.createdAt = aluno.getCreatedAt();
    }
}
