package com.trendsit.trendsit_fase2.dto.profile;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ProfileAdminDTO {
    private UUID id;
    private String username;
    private Integer idade;
    private String curso;
    private String diretorioNome;
    private Long friendNumber;
    private ProfileRole role;
    private LocalDateTime createdAt;

    public ProfileAdminDTO(Profile profile) {
        this.id = profile.getId();
        this.username = profile.getUsername();
        this.idade = profile.getIdade();
        this.curso = profile.getCurso();
        this.diretorioNome = profile.getDiretorioNome();
        this.friendNumber = profile.getFriendNumber();
        this.role = profile.getRole();
        this.createdAt = profile.getCreatedAt();
    }
}