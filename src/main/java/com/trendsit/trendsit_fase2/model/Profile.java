package com.trendsit.trendsit_fase2.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa o perfil de um usuário.
 * Implementa UserDetails para integração com Spring Security.
 */
@Entity
@Table(name = "profiles")
public class Profile implements UserDetails {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Enumerated(EnumType.ORDINAL)
    private ProfileRole role;

    private String nome;
    private Integer idade;
    private String curso;
    private LocalDateTime createdAt;

    // Construtores
    public Profile() {}
    public Profile(UUID id, Integer idade, String curso, LocalDateTime createdAt) {
        this.id = id;
        this.idade = idade;
        this.curso = curso;
        this.createdAt = createdAt;
    }

    public Profile(Integer idade, String curso) {
    }

    // Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ProfileRole getRole() { return role; }
    public void setRole(ProfileRole role) { this.role = role; }
    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // Métodos do UserDetails
    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return nome; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}