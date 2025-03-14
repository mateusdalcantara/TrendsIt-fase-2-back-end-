package com.trendsit.trendsit_fase2.model;

/**
 * Enum que representa os papéis de usuário no sistema.
 */
public enum ProfileRole {
    ADMIN("admin"),
    USER("user");

    private final String role;

    ProfileRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}