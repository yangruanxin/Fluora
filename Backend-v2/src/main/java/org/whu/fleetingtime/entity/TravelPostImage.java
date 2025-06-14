package org.whu.fleetingtime.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
//@SQLDelete(sql = "UPDATE travel_post_image SET deleted = true WHERE id = ?") // 逻辑删除SQL
@SoftDelete
public class TravelPostImage {
    @Id
    @Column(length = 36)
    private String id; // UUID

    // 多个TravelPostImage对应一个TravelPost
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "travel_post_id") // 外键列定义，允许为null，以便先创建Image再关联Post
    private TravelPost travelPost; // 直接引用TravelPost实体

    // --- 关联到User ---
    @Column(length = 36, nullable = false, updatable = false) // 图片所属用户的ID，不能为空，且通常创建后不应更改
    private String userId; // 直接存储用户ID字符串，记录图片是谁上传的/属于谁的

    @Column(nullable = false, length = 512)
    private String objectKey; // 阿里云OSS中的文件key

    private Integer sortOrder; // 图片的排序顺序

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @UpdateTimestamp
    private LocalDateTime updatedTime;


    @Column(nullable = false, insertable = false, updatable = false)
    private boolean deleted = false;

    @PrePersist
    protected void onPrePersist() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString(); // 自动生成ID
        }
        this.deleted = false;   // 确保新建时为未删除状态
    }
}