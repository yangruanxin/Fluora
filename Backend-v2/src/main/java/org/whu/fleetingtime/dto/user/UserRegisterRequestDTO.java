package org.whu.fleetingtime.dto.user;


import lombok.Data;

@Data
public class UserRegisterRequestDTO {
    private String username;
    private String password;
}
