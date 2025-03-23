package com.trendsit.trendsit_fase2.dto;

public record LoginResponse(
        String token,
        String username,
        String role
) {}
