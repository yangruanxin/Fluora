package org.whu.fleetingtime.dto.travelpost;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TravelPostResponseDTO {
    private Long postId;
    private Long userId;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private List<String> imageUrls;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}