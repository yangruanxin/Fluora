package org.whu.fleetingtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class TravelPostDetailsDTO {
    @Schema(description = "帖子ID")
    private String id;
    @Schema(description = "用户ID")
    private String userId;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "内容 (LONGTEXT)")
    private String content;
    @Schema(description = "地点名称")
    private String locationName;
    @Schema(description = "纬度")
    private Double latitude;
    @Schema(description = "经度")
    private Double longitude;
    @Schema(description = "开始时间")
    private LocalDateTime beginTime;
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
    @Schema(description = "最后更新时间")
    private LocalDateTime updatedTime;
    @Schema(description = "关联图片的预签名URL列表 (按顺序)")
    private List<String> imageUrls;
}
