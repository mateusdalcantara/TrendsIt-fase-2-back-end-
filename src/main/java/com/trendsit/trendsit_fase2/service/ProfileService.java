package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Serviço para operações relacionadas a perfis de usuários.
 */
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    /**
     * Construtor para injeção de dependência do repositório de perfis.
     * @param profileRepository Repositório JPA para operações com a tabela de perfis.
     */
    @Autowired
    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Cria ou atualiza um perfil do usuário autenticado.
     *
     * @param idade Idade do usuário.
     * @param curso Curso do usuário.
     * @return Perfil salvo no banco de dados.
     * @throws RuntimeException Se o usuário não estiver autenticado.
     */
    public Profile criarPerfil(Integer idade, String curso) {
        // Obtém o contexto de autenticação do Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // Extrai o ID do usuário do token JWT (armazenado no 'subject')
            String userId = authentication.getName();
            UUID uuid = UUID.fromString(userId);

            // Cria o objeto Profile com os dados
            Profile profile = new Profile(idade, curso);
            profile.setId(uuid); // Define o ID como o mesmo do usuário
            profile.setCreatedAt(LocalDateTime.now()); // Data de criação

            return profileRepository.save(profile); // Persiste no banco
        } else {
            throw new RuntimeException("Usuário não autenticado.");
        }
    }
}