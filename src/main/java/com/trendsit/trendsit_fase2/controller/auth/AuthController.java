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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.UUID;



@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ProfileRepository profileRepository;
    private final SupabaseAuthService supabaseAuthService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(ProfileRepository profileRepository, SupabaseAuthService supabaseAuthService) {
        this.profileRepository = profileRepository;
        this.supabaseAuthService = supabaseAuthService;
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
    public ResponseEntity<LoginResponse> login(@RequestHeader("Authorization") String authHeader) {
        // Decodifica e inspeciona token
        DecodedJWT jwt = JwtUtils.decodeToken(authHeader);
        System.out.println("JWT Claims: " + JwtUtils.getAllClaims(jwt));

        // Extrai userId do sub
        String userId = JwtUtils.getUserId(jwt);
        System.out.println("Usuário autenticado (sub): " + userId);

        // Busca ou cria Profile
        Profile profile = profileRepository.findById(UUID.fromString(userId))
                .orElseGet(() -> {
                    System.out.println("Criando novo Profile para ID " + userId);
                    Profile novo = new Profile();
                    novo.setId(UUID.fromString(userId));
                    // Se você armazenou username em user_metadata no Supabase:
                    Object um = jwt.getClaim("user_metadata").asMap().get("username");
                    novo.setUsername(um != null ? um.toString() : "user_" + userId.substring(0,8));
                    novo.setRole(ProfileRole.USER);
                    // Gere friendNumber único
                    novo.setFriendNumber(System.currentTimeMillis());
                    return profileRepository.save(novo);
                });

        // Retorna LoginResponse
        return ResponseEntity.ok(new LoginResponse(
                profile.getId().toString(),
                profile.getUsername(),
                profile.getRole().name()
        ));
    }
}
