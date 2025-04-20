package com.trendsit.trendsit_fase2.dto.postagem;

import com.trendsit.trendsit_fase2.model.postagem.Postagem;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PostagemDTO {
    private Long id;
    @NotBlank(message = "O conteúdo da postagem é obrigatório")
    private String conteudo;
    private LocalDateTime createdAt;
    private UUID autorId;
}