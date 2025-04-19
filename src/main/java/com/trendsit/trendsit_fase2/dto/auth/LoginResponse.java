package com.trendsit.trendsit_fase2.dto.auth;

import java.util.UUID;

public record LoginResponse(
        String token,    // o JWT
        UUID userId,     // o id do Profile
        String username,
        String role
) {}
