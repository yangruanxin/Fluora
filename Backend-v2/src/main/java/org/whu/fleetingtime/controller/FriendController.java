package org.whu.fleetingtime.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.entity.FriendRequest;
import org.whu.fleetingtime.service.FriendService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    // 发送好友请求
    @PostMapping("/request")
    public Result<Void> sendFriendRequest(@RequestParam String receiverId, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        friendService.sendFriendRequest(userId, receiverId);
        return Result.success("好友请求已发送", null);
    }

    // 响应好友请求（接受或拒绝）
    @PostMapping("/request/respond")
    public Result<Void> respondToFriendRequest(@RequestParam String action,@RequestParam String requestId,HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        friendService.respondToRequest(userId, requestId, action);
        return Result.success("好友请求已处理", null);
    }

    // 获取好友列表
    @GetMapping("/list")
    public Result<List<String>> getFriendList(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        List<String> friends = friendService.listFriends(userId);
        return Result.success(friends);
    }

    // 获取待处理的好友请求
    @GetMapping("/requests/pending")
    public Result<List<FriendRequest>> getPendingRequests(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        List<FriendRequest> pendingRequests = friendService.listPendingRequests(userId);
        return Result.success(pendingRequests);
    }

    // 删除好友
    @DeleteMapping
    public Result<Void> deleteFriend(@RequestParam String friendId,HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        friendService.deleteFriend(userId, friendId);
        return Result.success("好友已删除", null);
    }
}
