package com.trendsit.trendsit_fase2.dto.Admin;

import com.trendsit.trendsit_fase2.model.ProfileRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateAdminDTO {
    @NotBlank(message = "Adicionar um nome, é necessário.")
    private String username;

    private Integer idade;
    private String curso;
    private ProfileRole role; // Admin-specific field
}