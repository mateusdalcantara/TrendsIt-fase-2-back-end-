package com.trendsit.trendsit_fase2.dto.group;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupPostRequestDTO {
    @NotBlank(message = "O conteúdo da postagem é obrigatório")
    private String content;
}