package com.trendsit.trendsit_fase2.Model;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;


@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "curso", nullable = false)
    private String curso;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "role")
    private String role;
    public void setHash(String senha) {
        this.senhaHash = new BCryptPasswordEncoder().encode(senha);
    }

    @Column(name = "d_criacao", nullable = false)
    private LocalDateTime d_criacao;

    public Usuario(){};


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getD_criacao() {
        return d_criacao;
    }

    public void setD_criacao(LocalDateTime d_criacao) {
        this.d_criacao = d_criacao;
    }
}