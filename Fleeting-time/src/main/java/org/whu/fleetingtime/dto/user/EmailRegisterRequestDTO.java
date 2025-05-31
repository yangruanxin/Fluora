package org.whu.fleetingtime.dto.user;

import lombok.Data;

@Data
public class EmailRegisterRequestDTO {
    private String email;
    private String password;
    private String code;
}

