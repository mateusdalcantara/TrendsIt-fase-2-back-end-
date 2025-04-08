package com.trendsit.trendsit_fase2.config;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.service.profile.ProfileService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ProfileService profileService;
    private final String supabaseSecret;

    public JwtAuthenticationFilter(ProfileService profileService, String supabaseSecret) {
        this.profileService = profileService;
        this.supabaseSecret = supabaseSecret;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/auth/login")
                || path.startsWith("/auth/register")
                || path.startsWith("/auth/check-auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(supabaseSecret.getBytes(StandardCharsets.UTF_8))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                UUID userId = UUID.fromString(claims.getSubject());
                System.out.println("Usuário autenticado: " + userId);

                Profile profile = profileService.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Profile not found"));

                // Correct placement of authorities list
                List<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + profile.getRole().name().toUpperCase())
                );

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        profile,
                        null,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado. Faça login novamente.");
            return;
        } catch (Exception e) {
            System.out.println("Erro ao processar o token: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Autenticação inválida");
            return;
        }

        filterChain.doFilter(request, response);
    }
}