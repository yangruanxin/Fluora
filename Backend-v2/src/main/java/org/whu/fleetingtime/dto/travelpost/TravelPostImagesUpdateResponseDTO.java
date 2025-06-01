package org.whu.fleetingtime.dto.travelpost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TravelPostImagesUpdateResponseDTO {
    @Schema(description = "帖子ID")
    private String postId;

    @Schema(description = "更新后，帖子关联的图片URL列表 (按新顺序)")
    private List<String> finalImageUrls;

    @Schema(description = "帖子的最后更新时间")
    private LocalDateTime postUpdatedTime;
}