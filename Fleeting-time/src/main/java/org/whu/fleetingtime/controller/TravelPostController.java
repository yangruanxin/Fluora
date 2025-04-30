package org.whu.fleetingtime.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.dto.checkin.CheckinRequestDTO;
import org.whu.fleetingtime.dto.checkin.CheckinResponseDTO;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.service.TravelPostService;

@CrossOrigin
@RestController
@RequestMapping("/api/checkins")
public class TravelPostController {
    private static final Logger logger = LoggerFactory.getLogger(TravelPostController.class);

    @Autowired
    private TravelPostService checkinsService;

    @PostMapping
    public Result<CheckinResponseDTO> checkin(
            HttpServletRequest request,
            @RequestBody CheckinRequestDTO checkinRequestDTO
    ) {
        logger.info("【打卡请求】{}", checkinRequestDTO);
        // 从拦截器注入的 request 属性中拿 userId
        String userIdStr = (String) request.getAttribute("userId");
        Long userId = Long.parseLong(userIdStr);

        CheckinResponseDTO response = checkinsService.checkin(userId, checkinRequestDTO);
        logger.info("【打卡成功】{}", response);
        return Result.success("checkin success", response);
    }

}
