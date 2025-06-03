package org.whu.fleetingtime.dto.user;

import lombok.Data;

@Data
public class RecoverPasswordRequestDTO {
    private String email; // 可为空
    private String phone; // 可为空
    private String code;
    private String newPassword;
}
