package com.trendsit.trendsit_fase2.dto.Profile;

import com.trendsit.trendsit_fase2.model.Profile;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ProfilePublicoDTO {
    //private UUID id;
    private String username;
    private Integer idade;
    private String curso;
    private LocalDateTime createdAt;

    public ProfilePublicoDTO(Profile profile) {
        //this.id = profile.getId();
        this.username = profile.getUsername();
        this.idade = profile.getIdade();
        this.curso = profile.getCurso();
        this.createdAt = profile.getCreatedAt();
    }
}