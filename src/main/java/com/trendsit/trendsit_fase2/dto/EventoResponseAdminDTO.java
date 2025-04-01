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
public class EventoResponseAdminDTO {
    private Long id;
    private String titulo;
    private String conteudo;
    private LocalDateTime createdAt;
    private AutorDTO autor;
    private Boolean status;

    public EventoResponseAdminDTO(Evento evento) {
        this.id = evento.getId();
        this.titulo = evento.getTitulo();
        this.conteudo = evento.getConteudo();
        this.createdAt = evento.getCreatedAt();
        this.autor = new AutorDTO();
        this.autor.setUsername(evento.getAutor().getUsername());
        this.status = evento.getStatus();
    }
}
