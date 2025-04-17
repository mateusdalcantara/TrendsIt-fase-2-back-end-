package com.trendsit.trendsit_fase2.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtUtils {
    public static DecodedJWT decodeToken(String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;
        return JWT.decode(token);
    }

    public static String getUserId(DecodedJWT jwt) {
        return jwt.getSubject();
    }

    public static Map<String, Object> getAllClaims(DecodedJWT jwt) {
        return jwt.getClaims().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().as(Object.class)
                ));
    }
}
