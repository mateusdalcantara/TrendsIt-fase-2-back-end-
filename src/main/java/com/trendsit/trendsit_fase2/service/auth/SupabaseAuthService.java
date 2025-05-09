package com.trendsit.trendsit_fase2.service.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendsit.trendsit_fase2.service.profile.ProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SupabaseAuthService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ProfileService profileService;

    public SupabaseAuthService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            ProfileService profileService
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.profileService = profileService;
    }

    public String extractSupabaseToken(String responseBody) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("access_token").asText();
    }

    public ResponseEntity<String> registro(String email, String password, String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("user_metadata", Map.of("username", username));

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    supabaseUrl + "/auth/v1/signup",
                    new HttpEntity<>(body, headers),
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                UUID userId = extractUserIdFromResponse(response.getBody());
                profileService.createProfile(userId, username);
            }

            return response;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY &&
                    e.getResponseBodyAsString().contains("user_already_exists")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já registrado.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao registrar: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado no registro", e);
        }
    }


    public UUID extractUserIdFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return UUID.fromString(root.path("user").path("id").asText());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user ID", e);
        }
    }

    public String login(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "email", email,
                "password", password
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                supabaseUrl + "/auth/v1/token?grant_type=password",
                new HttpEntity<>(body, headers),
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(
                    "Falha ao autenticar: status " + response.getStatusCodeValue()
            );
        }

        try {
            // usa seu método existente para pegar só o access_token
            return extractSupabaseToken(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Não foi possível extrair access_token", e);
        }
    }
}