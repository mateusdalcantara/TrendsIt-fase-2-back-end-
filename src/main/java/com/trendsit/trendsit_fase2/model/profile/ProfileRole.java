package com.trendsit.trendsit_fase2.model.profile;

/**
 * Enum que representa os papéis de usuário no sistema.
 */
public enum ProfileRole {
    ALUNO("ALUNO"), //0
    ADMIN("ADMIN"), //1
    PROFESSOR("PROFESSOR");

    private final String role;

    ProfileRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}