package com.trendsit.trendsit_fase2.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.service.profile.ProfileService;
import com.trendsit.trendsit_fase2.util.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
            String token = header.substring(7);
            try {
                DecodedJWT jwt = JwtUtils.decodeToken(token);
                UUID userId = UUID.fromString(JwtUtils.getUserId(jwt));
                logger.debug("Authenticated user ID: {}", userId);

                // Load or create Profile
                Profile profile = profileService.findById(userId)
                        .orElseGet(() -> {
                            // extract username from metadata
                            Map<String, Object> meta = jwt.getClaim("user_metadata").asMap();
                            String username = Optional.ofNullable(meta.get("username"))
                                    .map(Object::toString)
                                    .orElse("user_" + userId.toString().substring(0, 8));
                            // create new profile
                            return profileService.createProfile(userId, username);
                        });

                List<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + profile.getRole().name())
                );
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(profile, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

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
        filterChain.doFilter(request, response);
    }
}
