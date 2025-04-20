package com.trendsit.trendsit_fase2.dto.group;

import com.trendsit.trendsit_fase2.dto.profile.SimpleProfileDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GroupResponseDTO {
    private UUID id;
    private String nome;
    private LocalDateTime createdAt;
    private SimpleProfileDTO criadorDoGrupo;
    private List<SimpleProfileDTO> membros;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SimpleProfileDTO getCriadorDoGrupo() {
        return criadorDoGrupo;
    }

    public void setCriadorDoGrupo(SimpleProfileDTO criadorDoGrupo) {
        this.criadorDoGrupo = criadorDoGrupo;
    }

    public List<SimpleProfileDTO> getMembros() {
        return membros;
    }

    public void setMembros(List<SimpleProfileDTO> membros) {
        this.membros = membros;
    }
}