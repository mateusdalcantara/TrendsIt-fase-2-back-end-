package com.trendsit.trendsit_fase2.dto;

import com.trendsit.trendsit_fase2.model.Profile;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AutorAdminDTO {
    private UUID id;
    private String username;

    public AutorAdminDTO(Profile autor) {
        this.id = autor.getId();
        this.username = autor.getUsername();
    }
}
