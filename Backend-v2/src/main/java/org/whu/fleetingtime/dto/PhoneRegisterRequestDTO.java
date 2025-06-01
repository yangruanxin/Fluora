package org.whu.fleetingtime.dto;

import lombok.Data;

@Data
public class PhoneRegisterRequestDTO {
    private String phone;
    private String password;
    private String code;
}
