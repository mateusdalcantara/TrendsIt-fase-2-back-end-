package com.trendsit.trendsit_fase2.dto.group;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupPostCommentRequestDTO {
    @NotBlank(message = "O conteúdo do comentário é obrigatório")
    private String content;
}