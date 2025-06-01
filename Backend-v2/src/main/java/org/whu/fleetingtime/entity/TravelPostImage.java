package org.whu.fleetingtime.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity // 告诉JPA这是一个实体类
@Table(name = "travelpostimage") // 可选，如果表名和类名不一致（通常建议用复数形式的表名）
@Data
public class TravelPostImage
{
    @Id
    @Column(length = 36)
    private String id; // UUID
    private String postId;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createdTime;
}
