package org.whu.fleetingtime.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SoftDelete;


@Entity // 告诉JPA这是一个实体类
@Data
@SoftDelete
public class User {
    @Id
    @GeneratedValue(generator = "uuid") // 自动生成 UUID
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(length = 36)
    private String id; // UUID

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    private String avatarUrl;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String email;

    private String phone;

    @Column(nullable = false, insertable = false, updatable = false)
    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}
