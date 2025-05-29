package org.whu.fleetingtime.dto.travelpost;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TravelPostImageUpdateResponseDTO
{
    private Long postId;
    private List<String> finalImageUrlsInOrder; // 按新顺序排列的图片URL列表
    private LocalDateTime postUpdatedTime;       // TravelPost 的最后更新时间
}
