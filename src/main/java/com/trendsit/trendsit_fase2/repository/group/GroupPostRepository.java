package com.trendsit.trendsit_fase2.repository.group;

import com.trendsit.trendsit_fase2.service.group.GroupPost;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupPostRepository extends JpaRepository<GroupPost, UUID> {
    // Método existente
    List<GroupPost> findByGroupId(UUID groupId);

    @Query("SELECT p FROM GroupPost p " +
            "LEFT JOIN FETCH p.group g " +
            "LEFT JOIN FETCH g.membros " +
            "WHERE p.id = :postId")
    Optional<GroupPost> findByIdWithGroupAndMembers(@Param("postId") UUID postId);

    @Query("SELECT p FROM GroupPost p " +
            "LEFT JOIN FETCH p.comments " +  // Carrega os comentários
            "WHERE p.group.id = :groupId")
    List<GroupPost> findByGroupIdWithComments(@Param("groupId") UUID groupId);
}

