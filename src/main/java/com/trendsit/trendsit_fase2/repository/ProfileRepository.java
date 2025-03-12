package com.trendsit.trendsit_fase2.repository;

import com.trendsit.trendsit_fase2.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}