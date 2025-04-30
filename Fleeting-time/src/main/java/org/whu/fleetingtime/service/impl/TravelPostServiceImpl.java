package org.whu.fleetingtime.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.whu.fleetingtime.dto.travelpost.TravelPostRequestDTO;
import org.whu.fleetingtime.dto.travelpost.TravelPostResponseDTO;
import org.whu.fleetingtime.exception.BizException;
import org.whu.fleetingtime.exception.BizExceptionEnum;
import org.whu.fleetingtime.mapper.TravelPostMapper;
import org.whu.fleetingtime.pojo.TravelPost;
import org.whu.fleetingtime.service.TravelPostService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TravelPostServiceImpl implements TravelPostService {

    private static final Logger logger = LoggerFactory.getLogger(TravelPostServiceImpl.class);

    @Autowired
    TravelPostMapper travelPostMapper;

    @Override
    public TravelPostResponseDTO createTravelPost(Long userId, TravelPostRequestDTO request) {
        logger.info("[createTravelPost]收到新建旅行记录请求");
        // 输入校验
        if (request.getContent() == null ||
                request.getContent().trim().isEmpty() ||
                request.getLocationName() == null ||
                request.getLocationName().trim().isEmpty() ||
                request.getLatitude() == null ||
                request.getLongitude() == null ||
                request.getBeginTime() == null ||
                request.getBeginTime().trim().isEmpty()
        ) {
            logger.warn("[createTravelPost]请求参数无效");
            throw new BizException(BizExceptionEnum.INVALID_POST_PARAMETER);
        }

        // 获取当前时间作为 createdTime 和 updatedTime
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 处理endTime
        if (request.getEndTime() == null || request.getEndTime().trim().isEmpty()) {
            request.setEndTime(request.getBeginTime());
        }

        // 构造旅行记录对象
        TravelPost post = new TravelPost();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setLocationName(request.getLocationName());
        post.setLatitude(request.getLatitude());
        post.setLongitude(request.getLongitude());
        post.setBeginTime(LocalDateTime.parse(request.getBeginTime(), fmt));
        post.setEndTime(LocalDateTime.parse(request.getEndTime(), fmt));
        post.setCreatedTime(now);
        post.setUpdatedTime(now);

        // 存入数据库
        travelPostMapper.insert(post);

        // 7. 构建响应
        return TravelPostResponseDTO.builder()
                .postId(post.getId())
                .userId(userId)
                .locationName(post.getLocationName())
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .beginTime(post.getBeginTime())
                .endTime(post.getEndTime())
                .build();
    }
}
