package com.trendsit.trendsit_fase2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendsit.trendsit_fase2.dto.AuthProfileDTO;
import com.trendsit.trendsit_fase2.dto.LoginResponse;
import com.trendsit.trendsit_fase2.exception.ProfileNaoEncontradoException;
import com.trendsit.trendsit_fase2.exception.RespostaInvalidaDoSupabaseException;
import com.trendsit.trendsit_fase2.service.LoginRequest;
import com.trendsit.trendsit_fase2.service.ProfileService;
import com.trendsit.trendsit_fase2.service.RegistroRequest;
import com.trendsit.trendsit_fase2.service.SupabaseAuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final SupabaseAuthService supabaseAuthService;
    private final ProfileService profileService;
    private final ObjectMapper objectMapper;

    public AuthController(
            SupabaseAuthService supabaseAuthService,
            ProfileService profileService,
            ObjectMapper objectMapper
    ) {
        this.supabaseAuthService = supabaseAuthService;
        this.profileService = profileService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistroRequest request) {
        try {
            return supabaseAuthService.registro(
                    request.getEmail(),
                    request.getPassword(),
                    request.getUsername()
            );
        } catch (Exception e) {
            logger.error("Erro no registro: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Erro interno no servidor");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            ResponseEntity<String> response = supabaseAuthService.login(
                    request.getEmail(),
                    request.getPassword()
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(response.getStatusCode()).build();
            }

            JsonNode responseBody = objectMapper.readTree(response.getBody());
            if (!responseBody.has("access_token")) {
                logger.warn("Resposta do Supabase sem access_token: {}", responseBody);
                throw new RespostaInvalidaDoSupabaseException("Resposta de autenticação inválida");
            }

            String supabaseToken = responseBody.get("access_token").asText();
            UUID userId = supabaseAuthService.extractUserIdFromResponse(response.getBody());

            AuthProfileDTO profile = profileService.findAuthProfileById(userId)
                    .orElseThrow(() -> new ProfileNaoEncontradoException(userId));

            logger.info("Login bem-sucedido para o usuário: {}", userId);

            return ResponseEntity.ok(new LoginResponse(
                    supabaseToken,
                    profile.username(),
                    profile.role().name()
            ));

        } catch (HttpClientErrorException e) {
            logger.error("Erro de cliente HTTP: {}", e.getStatusCode());
            return ResponseEntity.status(e.getStatusCode()).body(
                    new LoginResponse(null, null, e.getStatusText())
            );
        } catch (JsonProcessingException e) {
            logger.error("Erro de parsing JSON: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    new LoginResponse(null, null, "Formato de resposta inválido")
            );
        } catch (ProfileNaoEncontradoException e) {
            logger.error("Perfil não encontrado: {}", e.getMessage());
            return ResponseEntity.status(404).body(
                    new LoginResponse(null, null, e.getMessage())
            );
        } catch (RespostaInvalidaDoSupabaseException e) {
            logger.error("Resposta inválida do Supabase: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    new LoginResponse(null, null, e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Erro interno: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(
                    new LoginResponse(null, null, "Erro interno no servidor")
            );
        }
    }
}