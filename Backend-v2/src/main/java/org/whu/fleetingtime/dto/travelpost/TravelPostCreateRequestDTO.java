package org.whu.fleetingtime.dto.travelpost; // 请替换为你的实际包名

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class TravelPostCreateRequestDTO {

    @Schema(description = "帖子标题", example = "我的东京之旅")
    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    @NotBlank(message = "帖子内容不能为空")
    @Schema(description = "帖子主要内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "今天天气很好，去了浅草寺...")
    private String content;

    @NotBlank(message = "地点名称不能为空")
    @Schema(description = "地点名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "浅草寺")
    @Size(max = 255, message = "地点名称长度不能超过255个字符")
    private String locationName;

    @NotNull(message = "纬度不能为空")
    @Schema(description = "地理位置纬度", requiredMode = Schema.RequiredMode.REQUIRED, example = "35.7148")
    private Double latitude;

    @NotNull(message = "经度不能为空")
    @Schema(description = "地理位置经度", requiredMode = Schema.RequiredMode.REQUIRED, example = "139.7967")
    private Double longitude;

    @NotBlank(message = "开始时间不能为空")
    @Schema(description = "旅行开始时间 (格式: yyyy-MM-dd HH:mm:ss)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-07-15 09:00:00")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", message = "时间格式必须为 yyyy-MM-dd HH:mm:ss")
    private String beginTime;

    @Schema(description = "旅行结束时间 (格式: yyyy-MM-dd HH:mm:ss)，如果为空，则默认为开始时间", example = "2025-07-16 18:00:00")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", message = "时间格式必须为 yyyy-MM-dd HH:mm:ss")
    private String endTime; // 可选，Service层可以处理为空时等于beginTime

    @Valid // 对列表中的每个ImageAssociationDTO对象也进行校验
    @Schema(description = "要关联的图片信息列表 (图片ID和对应的排序)。如果不想关联图片，可以不传此字段或传空列表。")
    private List<ImageAssociationDTO> images; // 允许不传或为空列表
}