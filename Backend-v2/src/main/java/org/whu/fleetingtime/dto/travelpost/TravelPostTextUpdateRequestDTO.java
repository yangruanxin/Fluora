package org.whu.fleetingtime.dto.travelpost;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TravelPostTextUpdateRequestDTO {

    @Schema(description = "帖子新标题", example = "我的东京奇妙冒险 (更新版)")
    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title; // 允许更新为空字符串或null来清除标题

    @NotBlank(message = "帖子内容不能为空")
    @Schema(description = "帖子新内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "更新了在银座的购物体验...")
    private String content;

    @NotBlank(message = "地点名称不能为空")
    @Schema(description = "新地点名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "东京塔")
    @Size(max = 255, message = "地点名称长度不能超过255个字符")
    private String locationName;

    @NotNull(message = "纬度不能为空")
    @Schema(description = "新地理位置纬度", requiredMode = Schema.RequiredMode.REQUIRED, example = "35.6586")
    private Double latitude;

    @NotNull(message = "经度不能为空")
    @Schema(description = "新地理位置经度", requiredMode = Schema.RequiredMode.REQUIRED, example = "139.7454")
    private Double longitude;

    @NotBlank(message = "开始时间不能为空")
    @Schema(description = "新旅行开始时间 (格式: yyyy-MM-dd HH:mm:ss)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-07-15 08:00:00")
    private String beginTime;

    @Schema(description = "新旅行结束时间 (格式: yyyy-MM-dd HH:mm:ss)，如果为空，则默认为开始时间", example = "2025-07-16 20:00:00")
    private String endTime;
}