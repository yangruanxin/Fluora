package org.whu.fleetingtime.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TravelPost {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
