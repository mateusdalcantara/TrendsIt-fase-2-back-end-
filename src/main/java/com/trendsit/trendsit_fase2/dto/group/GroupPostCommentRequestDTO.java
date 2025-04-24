package com.trendsit.trendsit_fase2.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupPostCommentRequestDTO {
    @NotBlank(message = "O conteúdo do comentário é obrigatório")
    @Size(max = 500)
    private String content;
}