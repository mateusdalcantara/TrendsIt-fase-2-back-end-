package com.trendsit.trendsit_fase2.dto.Profile;

import com.trendsit.trendsit_fase2.model.ProfileRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileAdminUpdateDTO {
    @NotBlank(message = "Username é obrigatório")
    private String username;
    private Integer idade;
    private String curso;
    private ProfileRole role;
}