package com.trendsit.trendsit_fase2.dto.profile;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ProfilePublicoDTO {
    private String username;
    private Integer idade;
    private String curso;
    private Long friendNumber;
    private LocalDateTime createdAt;

    public ProfilePublicoDTO(Profile profile) {
        this.username = profile.getUsername();
        this.idade = profile.getIdade();
        this.curso = profile.getCurso();
        this.friendNumber = profile.getFriendNumber();
        this.createdAt = profile.getCreatedAt();
    }
}