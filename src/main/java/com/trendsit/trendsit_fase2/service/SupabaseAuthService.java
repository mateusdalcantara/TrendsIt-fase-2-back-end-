package com.trendsit.trendsit_fase2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para integração com a API de autenticação do Supabase.
 */
@Service
public class SupabaseAuthService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> registro(String email, String password) {
        return fazerRequisicaoAutenticacao("/auth/v1/signup", email, password);
    }

    public ResponseEntity<String> login(String email, String password) {
        return fazerRequisicaoAutenticacao("/auth/v1/token?grant_type=password", email, password);
    }

    public ResponseEntity<String> resetPassword(String email) {
        HttpHeaders headers = criarHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);

        return restTemplate.postForEntity(
                supabaseUrl + "/auth/v1/recover",
                new HttpEntity<>(body, headers),
                String.class
        );
    }

    private ResponseEntity<String> fazerRequisicaoAutenticacao(String endpoint, String email, String password) {
        HttpHeaders headers = criarHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        return restTemplate.postForEntity(
                supabaseUrl + endpoint,
                new HttpEntity<>(body, headers),
                String.class
        );
    }

    private HttpHeaders criarHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}