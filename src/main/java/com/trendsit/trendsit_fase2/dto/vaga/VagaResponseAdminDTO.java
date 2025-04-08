package com.trendsit.trendsit_fase2.dto.vaga;

import com.trendsit.trendsit_fase2.model.vaga.Vaga;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class VagaResponseAdminDTO {
    private Long id;
    private String titulo;
    private String conteudo;
    private String salario;
    private String local;
    private LocalDateTime createdAt;
    private Vaga.Status status;
    private String autorUsername;

    public VagaResponseAdminDTO(Vaga vaga) {
        this.id = vaga.getId();
        this.titulo = vaga.getTitulo();
        this.conteudo = vaga.getConteudo();
        this.salario = vaga.getSalario();
        this.local = vaga.getLocal();
        this.createdAt = vaga.getCreatedAt();
        this.status = vaga.getStatus();
        this.autorUsername = vaga.getAutor().getUsername();
    }
}