package org.whu.fleetingtime.dto.user;

import lombok.Data;

@Data
public class PhoneRegisterRequestDTO {
    private String phone;
    private String password;
    private String code;
}
