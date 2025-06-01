package org.whu.fleetingtime.dto.sms;

import lombok.Data;

@Data
public class CaptchaValidationDTO {
    private String uuid;
    private String code;
}
