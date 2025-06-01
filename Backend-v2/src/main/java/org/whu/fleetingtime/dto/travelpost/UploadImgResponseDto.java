package org.whu.fleetingtime.dto.travelpost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadImgResponseDto {
    private String imageId;      // 图片在数据库中的ID (TravelPostImage的ID)
    private String objectKey;    // 图片在OSS中的存储路径和文件名
    private String url;          // 图片的公开访问URL
    private LocalDateTime createdTime; // 图片记录的创建时间
}