package com.trendsit.trendsit_fase2.service;

public enum Role {
    ADMIN("admin"),
    ALUNO("aluno"),
    PROFESSOR("professor"),
    USUARIO("usuario");

    private String mostreORole;

    Role(String mostreORole) {
        this.mostreORole = mostreORole;
    }

    public String getMostreORole() {
        return mostreORole;
    }
}
