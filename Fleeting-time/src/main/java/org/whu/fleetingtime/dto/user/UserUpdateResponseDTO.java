package org.whu.fleetingtime.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserUpdateResponseDTO {
    private String username;
//    private String avatarUrl;
    private Boolean hasPasswordUpdated;
    private LocalDateTime updatedTime;
}
