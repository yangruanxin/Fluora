package org.whu.fleetingtime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.whu.fleetingtime.exception.BizException;
import org.whu.fleetingtime.exception.BizExceptionEnum;
import org.whu.fleetingtime.mapper.FriendMapper;
import org.whu.fleetingtime.pojo.FriendRequest;
import org.whu.fleetingtime.pojo.UserFriend;
import org.whu.fleetingtime.service.FriendService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendMapper friendMapper;

    @Override
    public boolean sendRequest(Long senderId, Long receiverId) {
        FriendRequest existing = friendMapper.selectFriendRequest(senderId, receiverId);
        if (existing != null && "pending".equals(existing.getStatus())) {
            throw new BizException(BizExceptionEnum.FRIEND_ALREADY_EXISTS);
        }
        FriendRequest request = new FriendRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setStatus("pending");
        request.setCreatedTime(LocalDateTime.now());
        return friendMapper.insertFriendRequest(request) == 1;
    }

    @Override
    public boolean acceptRequest(Long requestId) {
        FriendRequest request = friendMapper.selectRequestById(requestId);
        if (request == null || !"pending".equals(request.getStatus())) {
            throw new BizException(BizExceptionEnum.FRIEND_REQUEST_NOT_FOUND);
        }

        LocalDateTime now = LocalDateTime.now();
        friendMapper.updateFriendRequestStatus(requestId, "accepted", now);

        UserFriend f1 = new UserFriend();
        f1.setUserId(request.getSenderId());
        f1.setFriendId(request.getReceiverId());
        f1.setCreatedTime(now);

        UserFriend f2 = new UserFriend();
        f2.setUserId(request.getReceiverId());
        f2.setFriendId(request.getSenderId());
        f2.setCreatedTime(now);

        friendMapper.insertFriend(f1);
        friendMapper.insertFriend(f2);
        return true;
    }

    @Override
    public List<UserFriend> listFriends(Long userId) {
        return friendMapper.selectFriendsByUserId(userId);
    }

    @Override
    public List<FriendRequest> listReceivedRequests(Long userId) {
        return friendMapper.getReceivedRequests(userId);
    }
}
