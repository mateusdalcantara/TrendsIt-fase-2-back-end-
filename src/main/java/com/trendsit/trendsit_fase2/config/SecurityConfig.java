package com.trendsit.trendsit_fase2.config;

import com.trendsit.trendsit_fase2.repository.ProfileRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração principal de segurança da aplicação.
 * Habilita:
 * - Autenticação baseada em JWT
 * - Autorização baseada em roles
 * - Proteção de endpoints
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ProfileRepository profileRepository
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF para APIs stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sem sessões
                .addFilterBefore(new JwtAuthenticationFilter(profileRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Rotas públicas
                        .anyRequest().authenticated()); // Demais rotas protegidas

        return http.build();
    }
}