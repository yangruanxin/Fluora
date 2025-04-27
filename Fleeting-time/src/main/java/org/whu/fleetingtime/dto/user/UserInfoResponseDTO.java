package org.whu.fleetingtime.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserInfoResponseDTO {
    private Long id;
    private String username;
    private String avatarUrl;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
