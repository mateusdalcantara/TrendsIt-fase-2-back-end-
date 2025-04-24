package com.trendsit.trendsit_fase2.service.relationship;

import com.trendsit.trendsit_fase2.dto.relationship.FriendshipResponseDTO;
import com.trendsit.trendsit_fase2.model.friendship.Friendship;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public interface FriendshipService {

    Friendship acceptFriendRequest(Long friendshipId);
    void removeFriend(UUID userId, UUID friendId);
    Friendship sendFriendRequest(UUID fromUserId, UUID toUserId);
    List<Profile> getFriends(UUID userId);
    void declineFriendRequest(Long friendshipId, UUID currentUserId);
    void cancelFriendRequest(Long friendshipId, UUID currentUserId);
    List<Friendship> getPendingFriendRequests(UUID userId);
    void removeFriend(UUID userId, Long friendNumber);
    List<Friendship> getSentPendingRequests(UUID id);
    List<Friendship> getReceivedPendingRequests(UUID id);
    void removeFriendByFriendNumber(UUID id, Long friendNumber);

    void removeAllFriendships(Profile profile);

    @Transactional
    void deleteProfile(UUID profileId);
}
