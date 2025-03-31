package com.trendsit.trendsit_fase2.dto;

import com.trendsit.trendsit_fase2.model.Profile;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AutorDTO {
    private String username;

    public AutorDTO(Profile autor) {
        this.username = autor.getUsername();
    }
}
