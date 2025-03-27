package com.trendsit.trendsit_fase2.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        // Obtenha as roles do usuário atual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRoles = "N/A";

        if (auth != null && auth.getAuthorities() != null) {
            userRoles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", "));
        }

        // Construa a resposta de erro
        ErrorResponse errorResponse = new ErrorResponse(
                "Acesso negado: voce nao tem permissao para este recurso.",
                "Permissao necessaria: " + getRequiredRole(request),
                "Permissoes do usuario: " + userRoles
        );

        // Configure a resposta HTTP
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

    private String getRequiredRole(HttpServletRequest request) {
        String path = request.getServletPath();

        if (path.startsWith("/admin")) {
            return "ROLE_ADMIN";
        } else if (path.startsWith("/profiles")) {
            return "ROLE_USER ou ROLE_ADMIN";
        }
        return "AUTHENTICATED_USER";
    }
}