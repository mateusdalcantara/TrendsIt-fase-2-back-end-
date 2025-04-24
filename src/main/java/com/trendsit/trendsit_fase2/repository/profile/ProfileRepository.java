package com.trendsit.trendsit_fase2.repository.profile;

import com.trendsit.trendsit_fase2.dto.auth.AuthProfileDTO;
import com.trendsit.trendsit_fase2.model.diretorio.Diretorio;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import org.springframework.data.jpa.repository.EntityGraph;
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

    List<Profile> findByRole(ProfileRole role);

    List<Profile> findByDiretorio(Diretorio diretorio);

    Optional<Profile> findById(UUID userId);

    @Query("""
    SELECT p 
    FROM Profile p 
    WHERE p.diretorio.id = :diretorioId 
    AND p.role = 'ALUNO'
""")
    List<Profile> findAlunosByDiretorioId(@Param("diretorioId") Long diretorioId);

    @Query("SELECT p FROM Profile p WHERE p.diretorio.id = :diretorioId")
    List<Profile> findAllByDiretorioId(@Param("diretorioId") Long diretorioId);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.following")
    @EntityGraph(attributePaths = {"following"})
    List<Profile> findAllWithFollowing();
}