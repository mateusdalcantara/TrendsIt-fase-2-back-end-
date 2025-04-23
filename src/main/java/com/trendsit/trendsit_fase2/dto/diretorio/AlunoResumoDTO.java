package com.trendsit.trendsit_fase2.dto.diretorio;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlunoResumoDTO {
    private String username;
    private String curso;
    private Long friendNumber;

    public AlunoResumoDTO() {
    }

    public AlunoResumoDTO(Profile profile) {
        this.username = profile.getUsername();
        this.curso = profile.getCurso();
        this.friendNumber = profile.getFriendNumber();
    }
}