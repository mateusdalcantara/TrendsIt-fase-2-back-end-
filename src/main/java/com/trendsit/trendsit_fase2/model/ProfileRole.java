package com.trendsit.trendsit_fase2.model;

public enum ProfileRole {
    ADMIN("admin"),

    USER("user");

    private String Role;

    ProfileRole(String role){this.Role = role;}

    public String GetRole(){return Role;}
}
