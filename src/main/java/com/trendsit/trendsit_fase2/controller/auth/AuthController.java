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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final ProfileRepository profileRepository;
    private final SupabaseAuthService supabaseAuthService;

    public AuthController(ProfileRepository profileRepository,
                          SupabaseAuthService supabaseAuthService) {
        this.profileRepository = profileRepository;
        this.supabaseAuthService = supabaseAuthService;
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

}
