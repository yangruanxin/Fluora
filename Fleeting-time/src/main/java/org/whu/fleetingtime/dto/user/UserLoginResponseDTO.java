package org.whu.fleetingtime.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginResponseDTO {
    private String token;
}
