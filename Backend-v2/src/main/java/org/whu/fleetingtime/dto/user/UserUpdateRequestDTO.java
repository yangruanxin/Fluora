package org.whu.fleetingtime.dto.user;

import lombok.Data;

@Data
public class UserUpdateRequestDTO {
    private String username;
    private String  email;
    private String  phone;
    private String  originPassword;
    private String  password;
}
