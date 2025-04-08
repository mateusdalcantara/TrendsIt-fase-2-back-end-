package com.trendsit.trendsit_fase2.dto.autor;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AutorDTO {
    private String username;

    public AutorDTO(Profile autor) {
        this.username = autor.getUsername();
    }
}
