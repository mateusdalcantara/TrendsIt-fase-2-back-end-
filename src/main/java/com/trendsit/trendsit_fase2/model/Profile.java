package com.trendsit.trendsit_fase2.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.relational.core.sql.In;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @Column(name = "id", columnDefinition = "UUID") // ID é definido manualmente
    private UUID id;

    @Column(nullable = false)
    private Integer idade;

    @Column(nullable = false)
    private String curso;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Construtor completo
    public Profile(UUID id, Integer idade, String curso, LocalDateTime createdAt) {
        this.id = id;
        this.idade = idade;
        this.curso = curso;
        this.createdAt = createdAt;
    }

    // Construtor para criação de perfis
    public Profile(Integer idade, String curso) {
        this.idade = idade;
        this.curso = curso;
        this.createdAt = LocalDateTime.now(); // Define a data de criação
    }

    // Construtor vazio (obrigatório para JPA)
    public Profile() {
    }
    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}