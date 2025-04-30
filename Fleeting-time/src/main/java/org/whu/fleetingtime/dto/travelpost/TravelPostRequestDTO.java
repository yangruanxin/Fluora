package org.whu.fleetingtime.dto.travelpost;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class TravelPostRequestDTO {
    private String title;
    private String content;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String beginTime;
    private String endTime;
    private List<MultipartFile> images;
    private List<Integer> orders;
}