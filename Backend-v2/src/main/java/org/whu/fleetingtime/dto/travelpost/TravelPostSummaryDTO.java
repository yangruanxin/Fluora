package org.whu.fleetingtime.dto.travelpost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TravelPostSummaryDTO {

    @Schema(description = "帖子ID")
    private String id;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子内容")
    private String content;

    @Schema(description = "地点名称")
    private String locationName;

    @Schema(description = "经度")
    private Double longitude;

    @Schema(description = "纬度")
    private Double latitude;

    @Schema(description = "旅行开始时间")
    private LocalDateTime beginTime;

    @Schema(description = "旅行结束时间")
    private LocalDateTime endTime;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "帖子的第一张图片URL (或缩略图URL)")
    private String firstImageUrl; // 示例：只显示第一张图或封面图
}