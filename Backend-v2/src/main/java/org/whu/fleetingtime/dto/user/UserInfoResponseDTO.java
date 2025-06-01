package org.whu.fleetingtime.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoResponseDTO {
    private String id;
    private String username;
    private String email;
    private String phone;
    private String avatarUrl;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
