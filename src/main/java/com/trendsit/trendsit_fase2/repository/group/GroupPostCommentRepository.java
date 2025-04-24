package com.trendsit.trendsit_fase2.repository.group;

import com.trendsit.trendsit_fase2.service.group.GroupPostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupPostCommentRepository extends JpaRepository<GroupPostComment, UUID> {
    List<GroupPostComment> findByPostId(UUID postId);
    @Query("SELECT c FROM GroupPostComment c LEFT JOIN FETCH c.author WHERE c.id = :commentId")
    Optional<GroupPostComment> findByIdWithAuthor(UUID commentId);
    @Query("SELECT c FROM GroupPostComment c " +
            "LEFT JOIN FETCH c.author " +
            "LEFT JOIN FETCH c.post p " +
            "LEFT JOIN FETCH p.group " +
            "WHERE c.id = :commentId")
    Optional<GroupPostComment> findByIdWithPostAndGroupAndAuthor(UUID commentId);

}
