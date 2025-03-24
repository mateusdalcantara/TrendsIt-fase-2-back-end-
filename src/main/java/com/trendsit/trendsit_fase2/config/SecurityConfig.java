package com.trendsit.trendsit_fase2.config;

import com.trendsit.trendsit_fase2.service.ProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final ProfileService profileService;
    private final String supabaseSecret;

    public SecurityConfig(
            ProfileService profileService,
            @Value("${supabase.jwt.secret}") String supabaseSecret
    ) {
        this.profileService = profileService;
        this.supabaseSecret = supabaseSecret;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    // Libere todas as origens temporariamente para testes
                    config.setAllowedOrigins(List.of(
                            "https://trendit.bubbleapps.io",
                            "http://trendsitone.fly.dev",
                            "http://localhost:8080" // Para testes locais
                    ));
                    // Adicione métodos necessários
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    // Permita todos os headers
                    config.setAllowedHeaders(List.of("*"));
                    // Permita credenciais
                    config.setAllowCredentials(true);
                    return config;
                }))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/swagger-ui.html",
                                "/auth/login",
                                "/auth/register",
                                "/common-area",
                                "/api/post",
                                "/api/post/{postId}/comment",
                                "/protected-endpoint"
                        ).permitAll()
                        .requestMatchers("/admin-only").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(profileService, supabaseSecret),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}