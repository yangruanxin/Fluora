package org.whu.fleetingtime.pojo;

import lombok.Data;

@Data
public class TravelPostImage
{
    private Long id;
    private Long post_id;
    private String image_url;
    private String sortOrder;
}
