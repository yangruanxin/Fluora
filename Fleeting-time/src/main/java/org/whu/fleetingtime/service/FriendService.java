package org.whu.fleetingtime.service;

import org.whu.fleetingtime.pojo.FriendRequest;
import org.whu.fleetingtime.pojo.UserFriend;

import java.util.List;

public interface FriendService {
    boolean sendRequest(Long senderId, Long receiverId);
    boolean acceptRequest(Long requestId);
    List<UserFriend> listFriends(Long userId);
    List<FriendRequest> listReceivedRequests(Long userId);
}
