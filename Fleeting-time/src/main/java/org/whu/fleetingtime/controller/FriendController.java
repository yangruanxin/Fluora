package org.whu.fleetingtime.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.pojo.UserFriend;
import org.whu.fleetingtime.service.FriendService;
import org.whu.fleetingtime.pojo.FriendRequest;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/friend")
public class FriendController {
    @Autowired
    private FriendService friendService;

    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);

    @PostMapping("/request")
    public Result<String> sendRequest(HttpServletRequest request, @RequestParam long receiverId) {
        //Long senderId = (Long) request.getAttribute("userId");
        String userIdStr = (String) request.getAttribute("userId");
        Long senderId = Long.parseLong(userIdStr);
        friendService.sendRequest(senderId, receiverId);
        return Result.success("好友请求已发送");
    }

    @GetMapping("/requests")
    public Result<List<FriendRequest>> getReceivedRequests(HttpServletRequest request) {
        String userIdStr = (String) request.getAttribute("userId");
        Long userId = Long.parseLong(userIdStr);
        List<FriendRequest> requests = friendService.listReceivedRequests(userId);
        return Result.success("查询好友申请成功", requests);
    }
    @PostMapping("/accept")
    public Result<String> acceptRequest(@RequestParam Long requestId) {
        friendService.acceptRequest(requestId);
        return Result.success("已接受好友请求");
    }
    @PostMapping("/reject")
    public Result<String> rejectRequest(@RequestParam Long requestId) {
        friendService.rejectRequest(requestId);
        return Result.success("已拒绝好友请求");
    }
    @GetMapping("/list")
    public Result<List<UserFriend>> getFriends(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success("查询成功", friendService.listFriends(userId));
    }
}
