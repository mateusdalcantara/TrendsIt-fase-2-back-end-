package com.trendsit.trendsit_fase2.service.relationship;

import com.trendsit.trendsit_fase2.exception.ConflictException;
import com.trendsit.trendsit_fase2.model.friendship.Friendship;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.repository.relationship.FriendshipRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final ProfileRepository profileRepository;

    public FriendshipServiceImpl(
            FriendshipRepository friendshipRepository,
            ProfileRepository profileRepository
    ) {
        this.friendshipRepository = friendshipRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public Friendship acceptFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Friendship not found"));
        friendship.setStatus(Friendship.Status.ACCEPTED);
        return friendshipRepository.save(friendship); // Return the saved entity
    }

    @Override
    public void removeFriend(UUID userId, UUID friendId) {
        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Profile friend = profileRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend not found"));

        List<Friendship> friendships = friendshipRepository.findFriendshipsBetweenUsers(user, friend);

        friendships.stream()
                .filter(f -> f.getStatus() == Friendship.Status.ACCEPTED)
                .findFirst()
                .ifPresent(f -> {
                    f.setStatus(Friendship.Status.DELETED);
                    friendshipRepository.save(f);
                });
    }

    @Override
    public Friendship sendFriendRequest(UUID fromUserId, UUID toUserId) {
        Profile fromUser = profileRepository.findById(fromUserId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        Profile toUser = profileRepository.findById(toUserId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        // Check existing requests (including DELETED/DECLINED)
        Optional<Friendship> existingRequest = friendshipRepository.findByUserFromAndUserTo(fromUser, toUser);

        if (existingRequest.isPresent()) {
            Friendship request = existingRequest.get();
            if (request.getStatus() == Friendship.Status.DELETED || request.getStatus() == Friendship.Status.DECLINED) {
                // Reset to PENDING to allow re-sending
                request.setStatus(Friendship.Status.PENDING);
                return friendshipRepository.save(request);
            } else {
                throw new ConflictException("Friend request already exists");
            }
        }

        // Create new request if none exists
        Friendship request = new Friendship();
        request.setUserFrom(fromUser);
        request.setUserTo(toUser);
        request.setStatus(Friendship.Status.PENDING);
        return friendshipRepository.save(request);
    }

    @Override
    public List<Profile> getFriends(UUID userId) {
        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Friendship> friendships = friendshipRepository.findByUserAndStatus(
                user,
                Friendship.Status.ACCEPTED
        );

        return friendships.stream()
                .map(f -> f.getUserFrom().equals(user) ? f.getUserTo() : f.getUserFrom())
                .filter(profile -> !profile.getId().equals(userId))  // Remove self-friendships
                .distinct()
                .collect(Collectors.toList());
    }


    @Override
    public void declineFriendRequest(Long friendshipId, UUID currentUserId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found"));

        if (!friendship.getUserTo().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You can only decline requests sent to you");
        }

        friendship.setStatus(Friendship.Status.DECLINED);
        friendshipRepository.save(friendship);
    }

    @Override
    public void cancelFriendRequest(Long friendshipId, UUID currentUserId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found"));

        // Ensure the current user is the SENDER (userFrom)
        if (!friendship.getUserFrom().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You can only cancel your own requests");
        }

        // Mark as DECLINED (sender canceled)
        friendship.setStatus(Friendship.Status.DECLINED);
        friendshipRepository.save(friendship);
    }

    @Override
    public List<Friendship> getPendingFriendRequests(UUID userId) {
        // Fetch requests where the user is the RECEIVER (userTo) and status is PENDING
        return friendshipRepository.findByUserToIdAndStatus(userId, Friendship.Status.PENDING);
    }

    public List<Friendship> getReceivedPendingRequests(UUID userId) {
        return friendshipRepository.findByUserToIdAndStatus(userId, Friendship.Status.PENDING);
    }

    public List<Friendship> getSentPendingRequests(UUID userId) {
        return friendshipRepository.findByUserFromIdAndStatus(userId, Friendship.Status.PENDING);
    }

    @Override
    public void removeFriend(UUID userId, Long friendNumber) {
        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Profile friend = profileRepository.findByFriendNumber(friendNumber)
                .orElseThrow(() -> new EntityNotFoundException("Friend not found with number: " + friendNumber));

        // Delete friendship between user and friend
        friendshipRepository.deleteFriendshipBetweenUsers(user.getId(), friend.getId());
    }

    @Override
    @Transactional
    public void removeFriendByFriendNumber(UUID userId, Long friendNumber) {
        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Profile friend = profileRepository.findByFriendNumber(friendNumber)
                .orElseThrow(() -> new EntityNotFoundException("Friend not found"));

        // Delete the friendship
        friendshipRepository.deleteFriendshipBetweenUsers(user.getId(), friend.getId());
    }

}
