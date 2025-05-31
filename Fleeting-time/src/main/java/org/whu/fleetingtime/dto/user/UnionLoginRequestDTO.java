package org.whu.fleetingtime.dto.user;

import lombok.Data;

@Data
public class UnionLoginRequestDTO {
    private String identifier;
    private String password;
}
