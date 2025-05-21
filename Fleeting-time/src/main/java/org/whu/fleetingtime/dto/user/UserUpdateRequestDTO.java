package org.whu.fleetingtime.dto.user;

import lombok.Data;

@Data
public class UserUpdateRequestDTO {
//    private MultipartFile avatar;
    private String username;
    private String  originPassword;
    private String  password;
}
