package org.whu.fleetingtime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.entity.FriendRequest;
import org.whu.fleetingtime.service.FriendService;

import java.util.List;

@RestController
@RequestMapping("/api/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    // 发送好友请求
    @PostMapping("/request")
    public Result<Void> sendFriendRequest(@RequestParam String senderId,
                                          @RequestParam String receiverId) {
        friendService.sendFriendRequest(senderId, receiverId);
        return Result.success("好友请求已发送", null);
    }

    // 响应好友请求（接受或拒绝）
    @PostMapping("/request/respond")
    public Result<Void> respondToFriendRequest(@RequestParam String requestId,
                                               @RequestParam String action) {
        friendService.respondToRequest(requestId, action);
        return Result.success("好友请求已处理", null);
    }

    // 获取好友列表
    @GetMapping("/list")
    public Result<List<String>> getFriendList(@RequestParam String userId) {
        List<String> friends = friendService.listFriends(userId);
        return Result.success(friends);
    }

    // 获取待处理的好友请求
    @GetMapping("/requests/pending")
    public Result<List<FriendRequest>> getPendingRequests(@RequestParam String userId) {
        List<FriendRequest> pendingRequests = friendService.listPendingRequests(userId);
        return Result.success(pendingRequests);
    }

    // 删除好友
    @DeleteMapping
    public Result<Void> deleteFriend(@RequestParam String userId,
                                     @RequestParam String friendId) {
        friendService.deleteFriend(userId, friendId);
        return Result.success("好友已删除", null);
    }
}
