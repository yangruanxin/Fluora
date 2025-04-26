package org.whu.fleetingtime.dto.checkin;


import lombok.Data;

@Data
public class CheckinRequestDTO {
    private Double latitude;
    private Double longitude;
    private String city;
    private String province;
    private String timestamp;
    private String note;
    private String device;
}