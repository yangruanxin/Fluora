package org.whu.fleetingtime.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserUpdateResponseDTO {
    private String username;
    private String  email;
    private String  phone;
    private LocalDateTime updatedTime;
}
