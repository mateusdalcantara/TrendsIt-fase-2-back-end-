package com.trendsit.trendsit_fase2.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.service.profile.ProfileService;
import com.trendsit.trendsit_fase2.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final ProfileService profileService;
    private final byte[] signingKey;

    public JwtAuthenticationFilter(ProfileService profileService, String supabaseSecret) {
        this.profileService = profileService;
        this.signingKey = supabaseSecret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            // 1) remove the "Bearer " prefix
            String token = header.substring(7);

            try {
                // 2) decode the clean JWT
                DecodedJWT jwt = JwtUtils.decodeToken(token);

                // 3) extract userId from the token subject
                UUID userId = UUID.fromString(JwtUtils.getUserId(jwt));
                logger.debug("Authenticated user ID: {}", userId);

                // 4) load profile (or fail)
                Profile profile = profileService.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Profile not found for ID: " + userId));

                // 5) update last active timestamp via service
                profileService.updateLastActive(userId);

                // 6) build authorities and Authentication
                List<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + profile.getRole().name())
                );
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(profile, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException ex) {
                logger.warn("JWT expired: {}", ex.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado. Faça login novamente.");
                return;
            } catch (Exception ex) {
                logger.error("Erro ao validar token JWT: {}", ex.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Autenticação inválida");
                return;
            }
        }

        // continue filter chain if everything is OK (or no token present)
        filterChain.doFilter(request, response);
    }
}
