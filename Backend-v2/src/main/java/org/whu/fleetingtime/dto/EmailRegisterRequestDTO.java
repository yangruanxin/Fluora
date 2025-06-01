package org.whu.fleetingtime.dto;

import lombok.Data;

@Data
public class EmailRegisterRequestDTO {
    private String email;
    private String password;
    private String code;
}
