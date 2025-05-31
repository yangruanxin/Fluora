package org.whu.fleetingtime.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFriend {
    private Long id;
    private Long userId;
    private Long friendId;
    private LocalDateTime createdTime;
}