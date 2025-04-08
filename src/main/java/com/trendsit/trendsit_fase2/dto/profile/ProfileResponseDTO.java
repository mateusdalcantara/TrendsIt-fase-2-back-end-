package com.trendsit.trendsit_fase2.dto.profile;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ProfileResponseDTO {
    private UUID id;
    private String username;
    private Integer idade;
    private String curso;
    private ProfileRole role;
    private LocalDateTime createdAt;

    public ProfileResponseDTO(Profile profile) {
        this.id = profile.getId();
        this.username = profile.getUsername();
        this.idade = profile.getIdade();
        this.curso = profile.getCurso();
        this.role = profile.getRole();
        this.createdAt = profile.getCreatedAt();
    }
}