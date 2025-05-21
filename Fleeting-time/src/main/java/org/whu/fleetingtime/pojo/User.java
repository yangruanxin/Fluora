package org.whu.fleetingtime.pojo;

import lombok.*;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String avatarUrl;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
