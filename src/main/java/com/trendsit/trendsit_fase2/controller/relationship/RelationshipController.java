package com.trendsit.trendsit_fase2.controller.relationship;

import com.trendsit.trendsit_fase2.dto.relationship.AdminFriendDTO;
import com.trendsit.trendsit_fase2.dto.relationship.AdminFriendshipResponseDTO;
import com.trendsit.trendsit_fase2.dto.relationship.FollowerDTO;
import com.trendsit.trendsit_fase2.dto.relationship.FriendDTO;
import com.trendsit.trendsit_fase2.dto.relationship.FriendRequestDTO;
import com.trendsit.trendsit_fase2.dto.relationship.FriendshipResponseDTO;
import com.trendsit.trendsit_fase2.dto.relationship.RelationshipNotificationsDTO;
import com.trendsit.trendsit_fase2.dto.relationship.UpdateFriendNumberRequest;
import com.trendsit.trendsit_fase2.exception.ConflictException;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.exception.InvalidFriendNumberException;
import com.trendsit.trendsit_fase2.model.friendship.Friendship;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.relationship.FollowService;
import com.trendsit.trendsit_fase2.service.relationship.FriendshipService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RelationshipController {

    private final FriendshipService friendshipService;
    private final FollowService followService;
    private final ProfileRepository profileRepository;


    public RelationshipController(
            FriendshipService friendshipService,
            FollowService followService,
            ProfileRepository profileRepository
    ) {
        this.friendshipService = friendshipService;
        this.followService = followService;
        this.profileRepository = profileRepository;
    }

    @PostMapping("/friends/request/{friendNumber}")
    public ResponseEntity<?> sendFriendRequest(
            @PathVariable String friendNumber, // Recebe como String
            @AuthenticationPrincipal Profile currentUser
    ) {
        try {
            // 1. Tentar converter para Long
            Long friendNumberLong = Long.parseLong(friendNumber);

            // 2. Buscar usuário pelo número
            Profile targetUser = profileRepository.findByFriendNumber(friendNumberLong)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

            // 3. Enviar solicitação
            Friendship friendship = friendshipService.sendFriendRequest(
                    currentUser.getId(),
                    targetUser.getId()
            );

            return ResponseEntity.ok(new FriendshipResponseDTO(friendship, currentUser));

        } catch (NumberFormatException e) {
            // 4. Tratar números inválidos
            throw new InvalidFriendNumberException("Número de amigo inválido");
        }
    }

    @PutMapping("/friends/accept/{friendshipId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> acceptFriendRequest(
            @PathVariable Long friendshipId,
            @AuthenticationPrincipal Profile currentUser
    ) {
        Friendship friendship = friendshipService.acceptFriendRequest(friendshipId);

        if (currentUser.getRole().equals(ProfileRole.ADMIN)) {
            return ResponseEntity.ok(new AdminFriendshipResponseDTO(friendship, currentUser));
        } else {
            return ResponseEntity.ok(new FriendshipResponseDTO(friendship, currentUser));
        }
    }

    @PutMapping("/friend-number")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateFriendNumber(
            @RequestBody @Valid UpdateFriendNumberRequest request,
            @AuthenticationPrincipal Profile currentUser
    ) {
        // Verificar se o novo número já existe
        if (profileRepository.existsByFriendNumber(request.getNewFriendNumber())) {
            throw new ConflictException("Esse número está em uso, escolha outro.");
        }

        currentUser.setFriendNumber(request.getNewFriendNumber());
        profileRepository.save(currentUser);

        return ResponseEntity.ok().build();
    }

    // Follow Endpoints
    @PostMapping("/follow/{targetFriendNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> followUser(
            @PathVariable Long targetFriendNumber,
            @AuthenticationPrincipal Profile currentUser
    ) {
        followService.followUserByFriendNumber(currentUser.getId(), targetFriendNumber);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/follow/{targetFriendNumber}") // Changed from {targetUserId} to {targetFriendNumber}
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long targetFriendNumber, // Changed from UUID to Long
            @AuthenticationPrincipal Profile currentUser
    ) {
        followService.unfollowUserByFriendNumber(currentUser.getId(), targetFriendNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<RelationshipNotificationsDTO> getRelationshipNotifications(
            @AuthenticationPrincipal Profile currentUser
    ) {
        // Fetch received pending requests (requests sent TO the current user)
        List<Friendship> receivedPendingRequests =
                friendshipService.getReceivedPendingRequests(currentUser.getId());

        // Fetch sent pending requests (requests sent BY the current user)
        List<Friendship> sentPendingRequests =
                friendshipService.getSentPendingRequests(currentUser.getId());

        // Fetch followers
        List<FollowerDTO> followerDTOs = followService.getFollowers(currentUser.getId()).stream()
                .map(FollowerDTO::new)
                .toList();

        List<FriendRequestDTO> receivedDTOs = receivedPendingRequests.stream()
                .map(FriendRequestDTO::new)
                .toList();

        List<FriendRequestDTO> sentDTOs = sentPendingRequests.stream()
                .map(FriendRequestDTO::new)
                .toList();

        return ResponseEntity.ok(
                new RelationshipNotificationsDTO(receivedDTOs, sentDTOs, followerDTOs)
        );
    }

    @DeleteMapping("/friends/{friendNumber}")  // Changed from {friendId} to {friendNumber}
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> removeFriend(
            @PathVariable Long friendNumber,  // Changed from UUID to Long
            @AuthenticationPrincipal Profile currentUser
    ) {
        friendshipService.removeFriendByFriendNumber(currentUser.getId(), friendNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/friends")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getFriends(@AuthenticationPrincipal Profile currentUser) {
        List<Profile> friends = friendshipService.getFriends(currentUser.getId());

        if (currentUser.getRole().equals(ProfileRole.ADMIN)) {
            // Admins see IDs + friendNumber (via AdminFriendDTO)
            List<AdminFriendDTO> adminDtos = friends.stream()
                    .map(AdminFriendDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(adminDtos);
        } else {
            // Regular users see friendNumber (via FriendDTO)
            List<FriendDTO> userDtos = friends.stream()
                    .map(FriendDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userDtos);
        }
    }

    //O PATCH altera campo especifico.
    @PatchMapping("/friends/decline/{friendshipId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> declineFriendRequest(
            @PathVariable Long friendshipId,
            @AuthenticationPrincipal Profile currentUser
    ) {
        friendshipService.declineFriendRequest(friendshipId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/friends/cancel_friend_request/{friendshipId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> cancelFriendRequest(
            @PathVariable Long friendshipId,
            @AuthenticationPrincipal Profile currentUser
    ) {
        friendshipService.cancelFriendRequest(friendshipId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
