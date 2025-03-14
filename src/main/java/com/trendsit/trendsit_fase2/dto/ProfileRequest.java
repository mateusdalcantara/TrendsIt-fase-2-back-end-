package com.trendsit.trendsit_fase2.dto;

/**
 * DTO para criação/atualização de perfis.
 */
public class ProfileRequest {
    private Integer idade;
    private String curso;

    // Getters e Setters
    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
}