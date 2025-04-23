package com.trendsit.trendsit_fase2.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ComentarioCreateDTO {
    @NotBlank
    private String conteudo;
}

