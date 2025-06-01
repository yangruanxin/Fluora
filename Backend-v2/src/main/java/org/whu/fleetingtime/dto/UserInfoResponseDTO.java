package org.whu.fleetingtime.dto;

import lombok.Data;

@Data
public class UserInfoResponseDTO {
    private String username;
    private Boolean hasPasswordUpdated;
    //private LocalDateTime updatedTime;
}
