package com.trendsit.trendsit_fase2.service.relationship; // Fix package name typo

import com.trendsit.trendsit_fase2.model.profile.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface FollowService {
    void followUser(UUID followerId, UUID followingId);
    void unfollowUser(UUID followerId, UUID followingId);
    List<Profile> getFollowers(UUID userId);

    void followUserByFriendNumber(UUID id, Long targetFriendNumber);

    void unfollowUserByFriendNumber(UUID id, Long targetFriendNumber);
}