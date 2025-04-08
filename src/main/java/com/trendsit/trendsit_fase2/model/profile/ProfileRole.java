package com.trendsit.trendsit_fase2.model.profile;

/**
 * Enum que representa os papéis de usuário no sistema.
 */
public enum ProfileRole {
    USER("USER"), //0
    ADMIN("ADMIN"); //1

    private final String role;

    ProfileRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}