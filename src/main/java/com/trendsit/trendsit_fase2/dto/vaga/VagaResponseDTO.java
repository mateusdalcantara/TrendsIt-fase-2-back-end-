package com.trendsit.trendsit_fase2.dto.vaga;

import com.trendsit.trendsit_fase2.model.vaga.Vaga;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class VagaResponseDTO {
    private Long codigoVaga;
    private String titulo;
    private String conteudo;
    private String salario;
    private String local;
    private LocalDateTime createdAt;
    private String autorUsername;

    public VagaResponseDTO(Vaga vaga) {
        this.codigoVaga = vaga.getCodigoVaga();
        this.titulo = vaga.getTitulo();
        this.conteudo = vaga.getConteudo();
        this.salario = vaga.getSalario();
        this.local = vaga.getLocal();
        this.createdAt = vaga.getCreatedAt();
        this.autorUsername = vaga.getAutor().getUsername();
    }
}