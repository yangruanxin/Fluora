package org.whu.fleetingtime.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.dto.travelpost.TravelPostRequestDTO;
import org.whu.fleetingtime.dto.travelpost.TravelPostResponseDTO;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.service.TravelPostService;

@CrossOrigin
@RestController
@RequestMapping("/api/travel-posts")
public class TravelPostController {
    private static final Logger logger = LoggerFactory.getLogger(TravelPostController.class);

    @Autowired
    private TravelPostService travelPostService;

    @PostMapping
    public Result<TravelPostResponseDTO> createPost(
            HttpServletRequest request,
            @ModelAttribute TravelPostRequestDTO travelPostRequestDTO
    ) {
        // 从拦截器注入的 request 属性中拿 userId
        String userIdStr = (String) request.getAttribute("userId");
        Long userId = Long.parseLong(userIdStr);
        logger.info("【创建旅行记录请求】id: {}, {}", userIdStr, travelPostRequestDTO);
        //logger.info("看看数组长度, images:{}, order: {}", travelPostRequestDTO.getImages().size(), travelPostRequestDTO.getOrders().size());

        TravelPostResponseDTO response = travelPostService.createTravelPost(userId, travelPostRequestDTO);
        logger.info("【旅行记录创建成功】{}", response);
        return Result.success("post creation success", response);
    }
}
