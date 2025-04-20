package com.trendsit.trendsit_fase2.repository.group;

import com.trendsit.trendsit_fase2.service.group.GroupPostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupPostCommentRepository extends JpaRepository<GroupPostComment, UUID> {
    List<GroupPostComment> findByPostId(UUID postId);
}
