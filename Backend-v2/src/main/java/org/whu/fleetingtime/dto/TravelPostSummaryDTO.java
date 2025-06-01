package org.whu.fleetingtime.dto;

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

    @Schema(description = "地点名称")
    private String locationName;

    @Schema(description = "旅行开始时间")
    private LocalDateTime beginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "帖子的第一张图片URL (或缩略图URL)")
    private String firstImageUrl; // 示例：只显示第一张图或封面图

    // 或者，如果需要显示少量图片URL
    // @Schema(description = "图片URL列表 (摘要)")
    // private List<String> imageUrlsSummary;

    // 可以根据需要添加其他摘要字段，比如点赞数、评论数等（如果将来有）
}