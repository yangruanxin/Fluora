package org.whu.fleetingtime.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TravelPostImage
{
    private Long id;
    private Long postId;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createdTime;
}
