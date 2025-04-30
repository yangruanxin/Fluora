package org.whu.fleetingtime.dto.travelpost;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TravelPostResponseDTO {
    private Long postId;
    private Long userId;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}