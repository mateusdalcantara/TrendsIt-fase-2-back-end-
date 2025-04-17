package com.trendsit.trendsit_fase2.config;

import com.trendsit.trendsit_fase2.exception.CustomAccessDeniedHandler;
import com.trendsit.trendsit_fase2.exception.CustomAuthenticationEntryPoint;
import com.trendsit.trendsit_fase2.service.profile.ProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public SecurityConfig(
            ProfileService profileService,
            @Value("${supabase.jwt.secret}") String supabaseSecret,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint
    ) {
        this.profileService = profileService;
        this.supabaseSecret = supabaseSecret;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "https://trendit.bubbleapps.io",
                            "http://trendsitone.fly.dev",
                            "http://localhost:3000",
                            "http://localhost:4200",
                            "http://localhost:8080"
                    ));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/auth/check-auth"
                        ).permitAll()
                        // Allow USER/ADMIN to delete their own posts
                        .requestMatchers(HttpMethod.DELETE, "/api/post/**",
                               "/api/post/*/comentario/**",
                                "/api/friends/**",
                                "/api/follow/**",
                                "/profiles/{profileId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/events/**").authenticated()
                        .requestMatchers(HttpMethod.GET,
                                "/api/post",
                                "/api/post/**",
                                "/profiles",
                                "/events",
                                "/vagas"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/follow/**",
                                "/vagas").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/events/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/events/*/status").hasRole("ADMIN")

                        // Swagger/OpenAPI docs
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(profileService, supabaseSecret),
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );

        return http.build();
    }
}