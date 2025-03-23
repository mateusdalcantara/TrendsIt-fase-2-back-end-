package com.trendsit.trendsit_fase2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para criação/atualização de perfis.
 */
@Getter
@Setter
public class ProfileRequest {
    @NotBlank(message = "Username é obrigatório")
    private String username;
    private Integer idade;
    private String curso;

}