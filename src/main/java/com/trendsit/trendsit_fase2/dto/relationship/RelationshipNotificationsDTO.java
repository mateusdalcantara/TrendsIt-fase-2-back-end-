package com.trendsit.trendsit_fase2.dto.relationship;


import lombok.Getter;
import java.util.List;

@Getter
public class RelationshipNotificationsDTO {
    private final List<FriendRequestDTO> receivedPendingRequests;
    private final List<FriendRequestDTO> sentPendingRequests;
    private final List<FollowerDTO> followers;

    public RelationshipNotificationsDTO(
            List<FriendRequestDTO> receivedPendingRequests,
            List<FriendRequestDTO> sentPendingRequests,
            List<FollowerDTO> followers
    ) {
        this.receivedPendingRequests = receivedPendingRequests;
        this.sentPendingRequests = sentPendingRequests;
        this.followers = followers;
    }
}
