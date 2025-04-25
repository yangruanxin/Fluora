package org.whu.fleetingtime.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CheckinResponseDTO {
    private Long checkinId;
    private Double latitude;
    private Double longitude;
    private String city;
    private String province;
    private LocalDateTime timestamp;
    private String note;
    private String device;
    private Boolean isNewCity;
    private Integer highlightedCitiesCount;
}