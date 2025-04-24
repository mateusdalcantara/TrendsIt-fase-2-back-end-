package com.trendsit.trendsit_fase2.service.relationship;

import com.trendsit.trendsit_fase2.exception.ConflictException;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FollowServiceImpl implements FollowService {

    private final ProfileRepository profileRepository;

    public FollowServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public void followUser(UUID followerId, UUID followingId) {
        Profile follower = profileRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower not found"));

        Profile following = profileRepository.findById(followingId)
                .orElseThrow(() -> new EntityNotFoundException("User to follow not found"));

        if (follower.getFollowing().contains(following)) {
            throw new ConflictException("Already following this user");
        }

        follower.getFollowing().add(following);
        profileRepository.save(follower);
    }

    @Override
    @Transactional
    public void removeAllFollowRelations(Profile profile) {
        // Remove this profile from followers of others
        for (Profile followed : new ArrayList<>(profile.getFollowing())) {
            followed.getFollowers().remove(profile);
            profileRepository.save(followed);
        }

        // Remove others from followers of this profile
        for (Profile follower : new ArrayList<>(profile.getFollowers())) {
            follower.getFollowing().remove(profile);
            profileRepository.save(follower);
        }

        profile.getFollowing().clear();
        profile.getFollowers().clear();
        profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void unfollowUser(UUID followerId, UUID followingId) {
        Profile follower = profileRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower not found"));

        follower.getFollowing().removeIf(p -> p.getId().equals(followingId));
        profileRepository.save(follower);
    }

    @Override
    public List<Profile> getFollowers(UUID userId) {
        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new ArrayList<>(user.getFollowers());
    }

    @Override
    @Transactional
    public void followUserByFriendNumber(UUID followerId, Long targetFriendNumber) {
        Profile follower = profileRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower not found"));

        Profile targetUser = profileRepository.findByFriendNumber(targetFriendNumber)
                .orElseThrow(() -> new EntityNotFoundException("User with friend number " + targetFriendNumber + " not found"));

        if (follower.getFollowing().contains(targetUser)) {
            throw new ConflictException("Already following this user");
        }

        follower.getFollowing().add(targetUser);
        profileRepository.save(follower);
    }

    @Override
    @Transactional
    public void unfollowUserByFriendNumber(UUID followerId, Long targetFriendNumber) {
        Profile follower = profileRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower not found"));

        Profile targetUser = profileRepository.findByFriendNumber(targetFriendNumber)
                .orElseThrow(() -> new EntityNotFoundException("User with friend number " + targetFriendNumber + " not found"));

        boolean removed = follower.getFollowing().removeIf(p -> p.getId().equals(targetUser.getId()));
        if (!removed) {
            throw new EntityNotFoundException("Not following this user");
        }

        profileRepository.save(follower);
    }
}
