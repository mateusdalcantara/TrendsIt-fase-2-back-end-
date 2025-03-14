package com.trendsit.trendsit_fase2.config;

import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.model.ProfileRole;
import com.trendsit.trendsit_fase2.repository.ProfileRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * Filtro JWT para autenticação de requisições.
 * Responsável por:
 * - Extrair e validar tokens JWT
 * - Criar perfis automaticamente para novos usuários
 * - Configurar o contexto de segurança do Spring
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY = "Ev2zD3evxr64xjzxUAyxH8BeZWz3y3h7kd+FacCxMtVbqmXStvTYhSOPkJg7iWKxm0azpypls/prXbwk4opqHg==";
    private final ProfileRepository profileRepository;

    public JwtAuthenticationFilter(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // Processamento do token
                String userId = claims.getSubject();
                System.out.println("Usuário autenticado: " + userId);

                // Criação automática de perfil se necessário
                if (!profileRepository.existsById(UUID.fromString(userId))) {
                    criarPerfilPadrao(userId);
                }

                // Configuração da autenticação
                Profile profile = profileRepository.findById(UUID.fromString(userId)).orElseThrow();
                configurarAutenticacao(profile);

            } catch (ExpiredJwtException e) {
                System.out.println("Token expirado. Faça login novamente.");
            } catch (Exception e) {
                System.out.println("Erro ao processar o token: " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private void criarPerfilPadrao(String userId) {
        Profile profile = new Profile();
        profile.setId(UUID.fromString(userId));
        profile.setRole(ProfileRole.USER);
        profile.setIdade(0);
        profile.setCurso("Não informado");
        profile.setCreatedAt(LocalDateTime.now());
        profileRepository.save(profile);
        System.out.println("Novo perfil criado para: " + userId);
    }

    private void configurarAutenticacao(Profile profile) {
        Collection<? extends GrantedAuthority> authorities = profile.getAuthorities();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(profile, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}