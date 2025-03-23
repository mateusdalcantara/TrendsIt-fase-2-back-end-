package com.trendsit.trendsit_fase2.repository;

import com.trendsit.trendsit_fase2.dto.AuthProfileDTO;
import com.trendsit.trendsit_fase2.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByUsername(String username);
    Optional<AuthProfileDTO> findAuthProfileById(UUID userId);
    boolean existsById(UUID id);
    void deleteById(UUID id);
}