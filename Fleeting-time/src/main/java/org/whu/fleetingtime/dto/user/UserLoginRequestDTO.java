package org.whu.fleetingtime.dto.user;

import lombok.Data;

@Data
public class UserLoginRequestDTO {
    private String username;
    private String password;
}
