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
                    config.setAllowedOriginPatterns(List.of("*"));
                    config.setAllowedMethods(List.of("*"));
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
                                "/auth/check-auth",
                                "/auth/forgot-password",
                                "/auth/confirm-reset-password"

                        ).permitAll()

                        // GET public resources
                        .requestMatchers(HttpMethod.GET,
                                "/api/post",
                                "/api/post/**",
                                "/profiles",
                                "/events",
                                "/job"
                        ).permitAll()
                        // Endpoint específico do perfil do usuário autenticado
                        .requestMatchers(HttpMethod.GET, "/profiles/meu-perfil",
                                "/api/diretorio/meus-membros").authenticated()

                        // POST and PUT for creation/update, based on role
                        .requestMatchers(HttpMethod.POST,
                                "/api/post",
                                "/api/post/*/comentario",
                                "/api/groups",
                                "/api/groups/{groupId}/posts",
                                "/api/groups/{groupId}/posts/{postId}/comments",
                                "/api/follow/**",
                                "/events",
                                "/job/create-job-opportunity"
                        ).hasAnyRole("ALUNO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/events/**").hasAnyRole("ALUNO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/events/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/groups/**").hasAnyRole("ALUNO", "PROFESSOR", "ADMIN")

                        // DELETE endpoints: owners or admin
                        .requestMatchers(HttpMethod.DELETE,
                                // post comments
                                "/api/post/*/comentario/*",
                                // posts
                                "/api/post/**",
                                // group delete (creator or admin)
                                "/api/groups/**",
                                // group post/comments deletion
                                "/api/groups/*/posts/**",
                                "/api/groups/*/posts/*/comentario/**",
                                // relationship delete (friend, unfollow)
                                "/api/friends/**",
                                "/api/follow/**"
                        ).authenticated()

                        // DELETE directorate: only teacher or admin for removing aluno
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/diretorio/*/alunos/*"
                        ).hasAnyRole("ADMIN", "PROFESSOR")
                        // DELETE directorate itself: only admin
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/diretorio/**"
                        ).hasRole("ADMIN")

                        // DELETE events: only admin
                        .requestMatchers(HttpMethod.DELETE,
                                "/events/**"
                        ).hasRole("ADMIN")

                        // DELETE job opportunities: only admin
                        .requestMatchers(HttpMethod.DELETE,
                                "/job/**"
                        ).hasRole("ADMIN")

                        // DELETE profiles: only admin
                        .requestMatchers(HttpMethod.DELETE,
                                "/profiles/**"
                        ).hasRole("ADMIN")

                        // Fallback: any other DELETE requires admin
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                        // Swagger and static
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // Any other request: authenticated
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
