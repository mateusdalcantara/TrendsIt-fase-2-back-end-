package com.trendsit.trendsit_fase2.dto;

import com.trendsit.trendsit_fase2.model.Evento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventoResponseDTO {
    private String titulo;
    private String conteudo;
    private LocalDateTime createdAt;
    private AutorDTO autor;
    private Boolean status;

    public EventoResponseDTO(Evento evento) {
        this.titulo = evento.getTitulo();
        this.conteudo = evento.getConteudo();
        this.createdAt = getCreatedAt();
        this.autor = getAutor();
        this.status = false;
    }
}
