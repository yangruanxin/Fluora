package org.whu.fleetingtime.dto.travelpost;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TravelPostGetResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private List<String> imageUrls;
}