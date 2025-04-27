package org.whu.fleetingtime.dto.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserUpdateRequestDTO {
    private MultipartFile avatar;
    private String username;
    private String  originPassword;
    private String  password;
}
