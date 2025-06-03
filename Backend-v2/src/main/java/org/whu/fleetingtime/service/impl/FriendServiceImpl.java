package org.whu.fleetingtime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.whu.fleetingtime.entity.FriendRequest;
import org.whu.fleetingtime.entity.Friendship;
import org.whu.fleetingtime.exception.BizException;
import org.whu.fleetingtime.repository.FriendRequestRepository;
import org.whu.fleetingtime.repository.FriendshipRepository;
import org.whu.fleetingtime.service.FriendService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Override
    @Transactional
    public void sendFriendRequest(String senderId, String receiverId) {
        if (senderId.equals(receiverId)) {
            throw new BizException("不能添加自己为好友");
        }

        if (friendshipRepository.existsByUserIdAndFriendId(senderId, receiverId)) {
            throw new BizException("你们已经是好友了");
        }

        if (friendRequestRepository.findBySenderIdAndReceiverIdAndStatus(senderId, receiverId, "pending").isPresent()) {
            throw new BizException("已发送请求，请等待处理");
        }

        FriendRequest request = new FriendRequest();
        request.setId(UUID.randomUUID().toString());
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setStatus("pending");
        request.setCreatedTime(LocalDateTime.now());
        friendRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void respondToRequest(String userId, String requestId, String action) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BizException("请求不存在"));
        // 验证是否是当前用户收到的请求
        if (!request.getReceiverId().equals(userId)) {
            throw new BizException("无权处理该好友请求");
        }

        if (!"pending".equals(request.getStatus())) {
            throw new BizException("请求已处理");
        }

        if ("accept".equalsIgnoreCase(action)) {
            // 建立双向好友关系
            Friendship f1 = new Friendship();
            f1.setId(UUID.randomUUID().toString());
            f1.setUserId(request.getSenderId());
            f1.setFriendId(request.getReceiverId());
            f1.setCreatedTime(LocalDateTime.now());

            Friendship f2 = new Friendship();
            f2.setId(UUID.randomUUID().toString());
            f2.setUserId(request.getReceiverId());
            f2.setFriendId(request.getSenderId());
            f2.setCreatedTime(LocalDateTime.now());

            friendshipRepository.save(f1);
            friendshipRepository.save(f2);
            request.setStatus("accepted");
        } else if ("reject".equalsIgnoreCase(action)) {
            request.setStatus("rejected");
        } else {
            throw new BizException("不支持的操作类型");
        }

        request.setHandledTime(LocalDateTime.now());
        friendRequestRepository.save(request);
    }

    @Override
    public List<String> listFriends(String userId) {
        List<Friendship> list = friendshipRepository.findByUserId(userId);
        return list.stream().map(Friendship::getFriendId).collect(Collectors.toList());
    }

    @Override
    public List<FriendRequest> listPendingRequests(String userId) {
        return friendRequestRepository.findByReceiverIdAndStatus(userId, "pending");
    }

    @Override
    @Transactional
    public void deleteFriend(String userId, String friendId) {
        friendshipRepository.deleteByUserIdAndFriendId(userId, friendId);
        friendshipRepository.deleteByUserIdAndFriendId(friendId, userId);
    }
}
