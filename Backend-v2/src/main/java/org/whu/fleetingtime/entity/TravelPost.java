package org.whu.fleetingtime.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@SQLDelete(sql = "UPDATE travel_post SET deleted = true, updated_time = CURRENT_TIMESTAMP WHERE id = ?") // 逻辑删除SQL
public class TravelPost {
    @Id
    @Column(length = 36)
    private String id; // UUID

    @Column(length = 36, nullable = false)
    private String userId;

    @Column(length = 255)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(length = 255)
    private String locationName;

    private Double latitude;

    private Double longitude;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedTime;

    @Column(nullable = false) // 逻辑删除标记
    private boolean deleted = Boolean.FALSE;

    @OneToMany(
            mappedBy = "travelPost", // 指向TravelPostImage实体中名为'travelPost'的字段
            cascade = CascadeType.ALL, // 当TravelPost被（逻辑）删除时，关联的Image也会被级联（逻辑）删除
            orphanRemoval = true,    // 从images集合中移除Image时，该Image会被（逻辑）删除
            fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC")
    private List<TravelPostImage> images = new ArrayList<>();

    @PrePersist
    protected void onPrePersist() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // 维护双向关联的方法
    public void addImage(TravelPostImage image) {
        images.add(image);
        image.setTravelPost(this); // 关键：维护双向关联
    }

    public void removeImage(TravelPostImage image) {
        images.remove(image);
        image.setTravelPost(null); // 关键：解除双向关联
    }
}