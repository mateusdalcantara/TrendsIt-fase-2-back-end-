package com.trendsit.trendsit_fase2.exception;

public class ErrorResponse {
    private String message;
    private String requiredPermission;
    private String userPermission;

    public ErrorResponse(String message, String requiredPermission, String userPermission) {
        this.message = message;
        this.requiredPermission = requiredPermission;
        this.userPermission = userPermission;
    }

    // Getters e Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    public String getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(String userPermission) {
        this.userPermission = userPermission;
    }
}