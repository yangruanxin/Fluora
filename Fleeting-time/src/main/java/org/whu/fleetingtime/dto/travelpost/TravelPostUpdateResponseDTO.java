package org.whu.fleetingtime.dto.travelpost;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class TravelPostUpdateResponseDTO {
    private Long postId;
    private Long userId;
    private String title;
    private String content;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private LocalDateTime updatedTime; // 反映记录的最后更新时间
    private List<String> imageUrls; // 当前记录的图片 URL 列表 (此接口不修改图片，但返回当前状态)
}
