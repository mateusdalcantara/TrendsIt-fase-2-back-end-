package com.trendsit.trendsit_fase2.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventoDTO {
    private String titulo;
    private String conteudo;
    private LocalDateTime dataEvento;
    private String local;
}