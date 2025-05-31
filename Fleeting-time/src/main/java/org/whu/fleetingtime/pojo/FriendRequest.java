package org.whu.fleetingtime.pojo;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class FriendRequest {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String status; // pending, accepted, rejected
    private LocalDateTime createdTime;
    private LocalDateTime handledTime;
}