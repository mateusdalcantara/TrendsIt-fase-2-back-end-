package com.trendsit.trendsit_fase2.dto;

import com.trendsit.trendsit_fase2.util.OwnableEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoDTO {
    @NotBlank(message = "O título não pode estar vazio")
    private String titulo;

    @NotBlank(message = "O conteúdo não pode estar vazio")
    private String conteudo;

}