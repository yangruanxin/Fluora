package org.whu.fleetingtime.dto.travelpost;

import lombok.Data;

@Data
public class TravelPostTextUpdateRequestDTO {
    private String title; // 标题 (可选更新)
    private String content; // 内容 (必填)
    private String locationName; // 地点名称 (必填)
    private Double latitude; // 纬度 (必填)
    private Double longitude; // 经度 (必填)
    private String beginTime; // 开始时间, 格式: yyyy-MM-dd HH:mm:ss (必填)
    private String endTime;   // 结束时间, 格式: yyyy-MM-dd HH:mm:ss
}
