package org.whu.fleetingtime.pojo;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private Date createdTime;
    private Date updatedTime;
}
