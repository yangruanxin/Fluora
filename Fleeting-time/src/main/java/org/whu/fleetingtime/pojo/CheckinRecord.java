package org.whu.fleetingtime.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CheckinRecord {
    private Long id;
    private Long userId;
    private Double latitude;
    private Double longitude;
    private String city;
    private String province;
    private LocalDateTime timestamp;
    private String note;
    private String device;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
