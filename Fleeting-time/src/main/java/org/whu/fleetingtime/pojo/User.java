package org.whu.fleetingtime.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String avatarUrl;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    private String email;
    private String phone;
}
