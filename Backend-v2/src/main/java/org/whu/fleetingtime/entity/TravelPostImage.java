package org.whu.fleetingtime.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
// 移除了 org.hibernate.annotations.Filter

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@SQLDelete(sql = "UPDATE travel_post_image SET deleted = true WHERE id = ?") // 逻辑删除SQL
// @Table(name = "travel_post_image") // 如果表名不同，请取消注释并修改
public class TravelPostImage {
    @Id
    @Column(length = 36)
    private String id; // UUID

    // 多个TravelPostImage对应一个TravelPost
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_post_id") // 外键列定义，允许为null，以便先创建Image再关联Post
    private TravelPost travelPost; // 直接引用TravelPost实体

    @Column(nullable = false, length = 512)
    private String objectKey; // 阿里云OSS中的文件key

    private Integer sortOrder; // 图片的排序顺序

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false) // 逻辑删除标记
    private boolean deleted = Boolean.FALSE;

    @PrePersist
    protected void onPrePersist() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
    }
}