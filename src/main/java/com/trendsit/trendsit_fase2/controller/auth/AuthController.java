package com.trendsit.trendsit_fase2.controller.auth;

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

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> login(
            @RequestHeader("Authorization") String authHeader) {
        // extrai apenas o token (sem "Bearer ")
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        DecodedJWT jwt = JwtUtils.decodeToken(token);
        logger.debug("JWT Claims: {}", JwtUtils.getAllClaims(jwt));

        // sub vem como String UUID
        String sub = JwtUtils.getUserId(jwt);
        UUID userId = UUID.fromString(sub);

        // busca ou cria profile
        Profile profile = profileRepository.findById(userId)
                .orElseGet(() -> {
                    logger.info("Criando novo Profile para ID {}", sub);
                    Profile novo = new Profile();
                    novo.setId(userId);
                    Object um = jwt.getClaim("user_metadata")
                            .asMap()
                            .get("username");
                    novo.setUsername(
                            um != null
                                    ? um.toString()
                                    : "user_" + sub.substring(0, 8)
                    );
                    novo.setRole(ProfileRole.USER);
                    novo.setFriendNumber(System.currentTimeMillis());
                    return profileRepository.save(novo);
                });

        // retorna JWT + dados de profile
        LoginResponse resp = new LoginResponse(
                token,
                profile.getId(),
                profile.getUsername(),
                profile.getRole().name()
        );
        return ResponseEntity.ok(resp);
    }
}
