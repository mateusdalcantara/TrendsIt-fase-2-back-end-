package com.trendsit.trendsit_fase2.dto.profile;

import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileAdminUpdateDTO {
    @NotBlank(message = "Username é obrigatório")
    private String username;
    private Integer idade;
    private ProfileRole role;
}