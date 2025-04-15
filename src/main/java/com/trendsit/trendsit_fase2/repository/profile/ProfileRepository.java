package com.trendsit.trendsit_fase2.repository.profile;

import com.trendsit.trendsit_fase2.dto.auth.AuthProfileDTO;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByUsername(String username);
    Optional<AuthProfileDTO> findAuthProfileById(UUID userId);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    boolean existsByFriendNumber(Long friendNumber);
    Optional<Profile> findByFriendNumber(Long friendNumber);

    @Query("SELECT p FROM Profile p WHERE p.diretorio.id = :diretorioId")
    List<Profile> findAllByDiretorioId(@Param("diretorioId") Long diretorioId);

}