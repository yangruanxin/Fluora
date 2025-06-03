package org.whu.fleetingtime.dto.user;

import lombok.Data;

@Data
public class UserUpdateRequestDTO {
    private String username;
    private String  email;
    private String  emailcode;
    private String  phone;
    private String  phonecode;
    private String  originPassword;
    private String  password;
}
