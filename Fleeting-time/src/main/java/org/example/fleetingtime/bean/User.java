package org.example.fleetingtime.bean;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;
    private String password;
    private Long id;
}
