package com.trendsit.trendsit_fase2.dto.profile;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessorDiretorioDTO {
    private String username;
    private String friendNumber;

    public ProfessorDiretorioDTO(Profile professor) {
        this.username = professor.getUsername();
        this.friendNumber = String.valueOf(professor.getFriendNumber());
    }
}
