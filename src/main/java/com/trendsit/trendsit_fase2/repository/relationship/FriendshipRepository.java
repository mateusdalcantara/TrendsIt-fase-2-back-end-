package com.trendsit.trendsit_fase2.repository.relationship;

import com.trendsit.trendsit_fase2.model.friendship.Friendship;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findByUserFromAndUserTo(Profile userFrom, Profile userTo);
    List<Friendship> findByUserToAndStatus(Profile userTo, Friendship.Status status);
    List<Friendship> findByUserFromOrUserToAndStatus(Profile userFrom, Profile userTo, Friendship.Status status);
    boolean existsByUserFromAndUserTo(Profile userFrom, Profile userTo);
    void deleteByUserFromAndUserTo(Profile userFrom, Profile userTo);

    @Modifying
    @Query("DELETE FROM Friendship f WHERE " +
            "(f.userFrom.id = :userId1 AND f.userTo.id = :userId2) OR " +
            "(f.userFrom.id = :userId2 AND f.userTo.id = :userId1)")
    void deleteFriendshipBetweenUsers(@Param("userId1") UUID userId1,
                                      @Param("userId2") UUID userId2);

    @Query("SELECT f FROM Friendship f WHERE (f.userFrom = :user1 AND f.userTo = :user2) OR (f.userFrom = :user2 AND f.userTo = :user1)")
    List<Friendship> findFriendshipsBetweenUsers(@Param("user1") Profile user1, @Param("user2") Profile user2);

    @Query("SELECT f FROM Friendship f " +
            "LEFT JOIN FETCH f.userFrom " +  // Load all userFrom fields
            "LEFT JOIN FETCH f.userTo " +    // Load all userTo fields
            "WHERE (f.userFrom = :user OR f.userTo = :user) " +
            "AND f.status = :status")
    List<Friendship> findByUserAndStatus(
            @Param("user") Profile user,
            @Param("status") Friendship.Status status
    );

    @Query("SELECT f FROM Friendship f WHERE f.userTo = :user AND f.status = 'PENDING'")
    List<Friendship> findPendingRequestsForUser(@Param("user") Profile user);

    Optional<Friendship> findByIdAndStatus(Long id, Friendship.Status status);

    List<Friendship> findByUserToIdAndStatus(UUID userId, Friendship.Status status);
    List<Friendship> findByUserFromIdAndStatus(UUID userId, Friendship.Status status);
}