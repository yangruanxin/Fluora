package org.whu.fleetingtime.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Entity // 告诉JPA这是一个实体类
//@Table(name = "user")
@Data


public class User {
    @Id
    @Column(length = 36)
    private String id; // UUID

    private String username;

    private String password;

    private String avatarUrl;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String email;

    private String phone;
}
