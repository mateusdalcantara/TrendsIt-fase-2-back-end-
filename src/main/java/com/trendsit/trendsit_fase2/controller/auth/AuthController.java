package com.trendsit.trendsit_fase2.controller.auth;

import com.trendsit.trendsit_fase2.dto.auth.AuthRequest;
import com.trendsit.trendsit_fase2.dto.auth.LoginResponse;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.auth.RegistroRequest;
import com.trendsit.trendsit_fase2.service.auth.SupabaseAuthService;
import com.trendsit.trendsit_fase2.util.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final ProfileRepository profileRepository;
    private final SupabaseAuthService supabaseAuthService;
    private final RestTemplate restTemplate;

    public AuthController(ProfileRepository profileRepository,
                          SupabaseAuthService supabaseAuthService,
                          RestTemplate restTemplate) {
        this.profileRepository = profileRepository;
        this.supabaseAuthService = supabaseAuthService;
        this.restTemplate = restTemplate;
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@Valid @RequestBody RegistroRequest request) {
        try {
            return supabaseAuthService.registro(
                    request.getEmail(),
                    request.getPassword(),
                    request.getUsername()
            );
        } catch (Exception e) {
            logger.error("Erro no registro:", e);
            return ResponseEntity
                    .status(500)
                    .body("Erro interno no servidor");
        }
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody AuthRequest request) {
        // Autentica credenciais e recebe JWT do Supabase
        String token = String.valueOf(supabaseAuthService.login(request.getEmail(), request.getPassword()));
        logger.debug("JWT recebido: {}", token);

        // Decodifica o token para extrair o subject (userId)
        DecodedJWT jwt = JwtUtils.decodeToken(token);
        String sub = JwtUtils.getUserId(jwt);
        UUID userId = UUID.fromString(sub);

        // Busca ou cria profile
        Profile profile = profileRepository.findById(userId)
                .orElseGet(() -> {
                    logger.info("Criando novo Profile para ID {}", sub);
                    Profile novo = new Profile();
                    novo.setId(userId);
                    Object usernameFromMetadata = jwt.getClaim("user_metadata").asMap().get("username");

                    // Define o username (usa o valor do metadata se existir, ou só "usuario_" + 4 chars, ou "" se preferir forçar login via front)
                    novo.setUsername(usernameFromMetadata != null ? usernameFromMetadata.toString() : "");

                    novo.setRole(ProfileRole.ALUNO);
                    novo.setFriendNumber(System.currentTimeMillis());
                    return profileRepository.save(novo);
                });

        // Retorna token e dados do usuário
        LoginResponse response = new LoginResponse(
                token,
                profile.getId(),
                profile.getUsername(),
                profile.getRole().name()
        );
        return ResponseEntity.ok(response);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseKey);

        String requestBody = String.format("{\"email\":\"%s\", \"type\":\"recovery\"}", email);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    supabaseUrl + "/auth/v1/recover",
                    entity,
                    String.class
            );
            return ResponseEntity.ok().body(Map.of("message", "Senha nova enviada para o e-mail!"));
        } catch (Exception e) {
            logger.error("Erro ao enviar OTP: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao enviar OTP: " + e.getMessage()));
        }
    }
    @PostMapping("/confirm-reset-password")
    public ResponseEntity<?> confirmResetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword
    ) {
        // 1) Header com apikey + Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseKey);
        headers.setBearerAuth(token);

        // 2) Corpo da requisição
        HttpEntity<Map<String, String>> body = new HttpEntity<>(
                Map.of("password", newPassword),
                headers
        );

        try {
            // 3) Chama o Supabase para atualizar a senha do usuário
            restTemplate.exchange(
                    supabaseUrl + "/auth/v1/user",
                    HttpMethod.PUT,
                    body,
                    Void.class
            );
            return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso!"));
        } catch (HttpClientErrorException.Unauthorized e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido ou expirado"));
        } catch (Exception e) {
            logger.error("Erro no confirm-reset-password:", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno: " + e.getMessage()));
        }
    }

}
