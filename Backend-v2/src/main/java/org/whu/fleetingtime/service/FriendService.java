package org.whu.fleetingtime.service;

import org.whu.fleetingtime.entity.FriendRequest;

import java.util.List;

public interface FriendService {
    void sendFriendRequest(String senderId, String receiverId);
    void respondToRequest(String requestId, String action); // accept or reject
    List<String> listFriends(String userId);
    List<FriendRequest> listPendingRequests(String userId);
    void deleteFriend(String userId, String friendId);
}
