package com.trendsit.trendsit_fase2.dto.auth;

public record LoginResponse(
        String token,
        String username,
        String role
) {}
