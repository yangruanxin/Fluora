package org.whu.fleetingtime.dto.travelpost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class TravelPostCreateResponseDTO {
    @Schema(description = "新创建的帖子的ID")
    private String postId;

    @Schema(description = "帖子所属用户的ID")
    private String userId;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "地点名称")
    private String locationName;

    @Schema(description = "关联图片的预签名URL列表 (按顺序)")
    private List<String> imageUrls;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
}
