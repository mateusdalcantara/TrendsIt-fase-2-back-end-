package com.trendsit.trendsit_fase2.repository.group;

import com.trendsit.trendsit_fase2.model.group.Group;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    void deleteById(UUID id);
    List<Group> findByMembrosContaining(Profile member);
    List<Group> findByCriadorOrMembrosContaining(Profile criador, Profile membro);
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.membros WHERE g.id = :id")
    Optional<Group> findByIdWithMembros(@Param("id") UUID id);

    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.criador WHERE g.id = :id")
    Optional<Group> findByIdWithCreator(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Group g SET g.membros = FUNCTION('REMOVE_ELEMENT', g.membros, :profile)")
    void removeProfileFromAllGroups(@Param("profile") Profile profile);

    List<Group> findByCriador(Profile criador);

    @Modifying
    @Query("DELETE FROM GroupInvitation i WHERE i.invited = :profile")
    void deleteInvitationsByProfile(@Param("profile") Profile profile);

}

