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
        if (image != null) {
            images.add(image);          // 将图片添加到当前帖子的图片列表中
            image.setTravelPost(this); // 设置图片实体的travelPost字段指向当前帖子
            // 关键：当图片被关联到这个帖子时，确保图片的userId与帖子的userId一致
            // 或者，如果图片上传时已独立记录了上传者userId，这里可以进行校验或根据业务逻辑决定是否覆盖
            if (this.userId != null) {
                image.setUserId(this.userId); // 将帖子的userId赋予图片，确保归属一致性
                // 如果图片的userId是记录“上传者”，而帖子的userId是“帖子所有者”，
                // 且它们可能不同（比如协作场景），则此逻辑需要调整。
                // 但对于“私人旅行记录”，它们通常是同一个用户。
            }
        }
    }

    public void removeImage(TravelPostImage image) {
        if (image != null) {
            images.remove(image);       // 从当前帖子的图片列表中移除图片
            image.setTravelPost(null); // 解除图片实体与当前帖子的关联
            // 注意：此时图片的 userId 仍然保留，因为它记录的是图片的上传者/原始拥有者。
            // 如果业务逻辑要求解除与帖子的关联后，图片也变为无主（或需要其他处理），则需要额外逻辑。
        }
    }
}