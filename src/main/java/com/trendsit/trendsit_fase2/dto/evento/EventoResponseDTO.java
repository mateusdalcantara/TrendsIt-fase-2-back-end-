package com.trendsit.trendsit_fase2.dto.evento;

import com.trendsit.trendsit_fase2.model.evento.Evento;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class EventoResponseDTO {
    private String titulo;
    private String conteudo;
    private LocalDateTime dataEvento;
    private String local;
    private LocalDateTime createdAt;
    private String autorUsername;

    public EventoResponseDTO(Evento evento) {
        this.titulo = evento.getTitulo();
        this.conteudo = evento.getConteudo();
        this.dataEvento = evento.getDataEvento();
        this.local = evento.getLocal();
        this.createdAt = evento.getCreatedAt();
        this.autorUsername = evento.getAutor().getUsername();
    }
}