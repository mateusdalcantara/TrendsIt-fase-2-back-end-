package com.trendsit.trendsit_fase2.repository;

import com.trendsit.trendsit_fase2.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

// ProfileRepository.java
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    UserDetails findByNome(String nome);
}