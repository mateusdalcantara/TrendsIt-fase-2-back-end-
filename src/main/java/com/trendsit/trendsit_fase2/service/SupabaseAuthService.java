package com.trendsit.trendsit_fase2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseAuthService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Registrar usuário
    public ResponseEntity<String> registro(String email, String password) {
        String url = supabaseUrl + "/auth/v1/signup";
        return makeAuthRequest(url, email, password);
    }

    // Login do usuário
    public ResponseEntity<String> login(String email, String password) {
        String url = supabaseUrl + "/auth/v1/token?grant_type=password";
        return makeAuthRequest(url, email, password);
    }

    // Recuperação de senha
    public ResponseEntity<String> resetPassword(String email) {
        String url = supabaseUrl + "/auth/v1/recover";
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(url, request, String.class);
    }

    private ResponseEntity<String> makeAuthRequest(String url, String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(url, request, String.class);
    }
}