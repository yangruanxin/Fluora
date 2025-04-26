package org.whu.fleetingtime.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Data
public class User {
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private Date createdTime;
    private Date updatedTime;
}
