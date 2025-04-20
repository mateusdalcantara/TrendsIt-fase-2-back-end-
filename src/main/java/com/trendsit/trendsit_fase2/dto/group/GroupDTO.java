package com.trendsit.trendsit_fase2.dto.group;

import com.trendsit.trendsit_fase2.dto.profile.SimpleProfileDTO;
import com.trendsit.trendsit_fase2.model.group.Group;

import java.time.LocalDateTime;
import java.util.UUID;

public class GroupDTO {
    private UUID id;
    private String nome;
    private LocalDateTime createdAt;
    private SimpleProfileDTO criador;

    public GroupDTO(Group group) {
        this.id = group.getId();
        this.nome = group.getNome();
        this.createdAt = group.getCreatedAt();
        this.criador = new SimpleProfileDTO(group.getCriador());
    }

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

    public SimpleProfileDTO getCriador() {
        return criador;
    }

    public void setCriador(SimpleProfileDTO criador) {
        this.criador = criador;
    }
}
