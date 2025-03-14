package com.trendsit.trendsit_fase2.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.relational.core.sql.In;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class Profile implements UserDetails {

    @Id
    @Column(name = "id", columnDefinition = "UUID") // ID é definido manualmente
    private UUID id;

    @Enumerated(EnumType.ORDINAL)
    private ProfileRole role;
    public ProfileRole getRole() { return role; }
    public void setRole(ProfileRole role) { this.role = role; }
    @Column
    private String nome;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return nome;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}