package org.whu.fleetingtime.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity // 告诉JPA这是一个实体类
@Table(name = "travelpost") // 可选，如果表名和类名不一致（通常建议用复数形式的表名）
@Data
public class TravelPost {
    @Id
    @Column(length = 36)
    private String id; // UUID

    private String userId;
    private String title;
    private String content;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
