package org.whu.fleetingtime.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String identifier;
    private String password;
}
