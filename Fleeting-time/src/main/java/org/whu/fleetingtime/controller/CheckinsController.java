package org.whu.fleetingtime.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.dto.checkin.CheckinRequestDTO;
import org.whu.fleetingtime.dto.checkin.CheckinResponseDTO;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.service.CheckinsService;

@CrossOrigin
@RestController
@RequestMapping("/api/checkins")
public class CheckinsController {
    private static final Logger logger = LoggerFactory.getLogger(CheckinsController.class);

    @Autowired
    private CheckinsService checkinsService;

    @PostMapping("/checkin")
    public Result<CheckinResponseDTO> checkin(
            @RequestHeader("Authorization") String token,
            @RequestBody CheckinRequestDTO request
    ) {
        logger.info("【打卡请求】{}", request);
        CheckinResponseDTO response = checkinsService.checkin(token, request);
        logger.info("【打卡成功】{}", response);
        return Result.success("checkin success", response);
    }

}
