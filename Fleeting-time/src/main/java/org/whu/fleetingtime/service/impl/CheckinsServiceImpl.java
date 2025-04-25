package org.whu.fleetingtime.service.impl;

import org.springframework.stereotype.Service;
import org.whu.fleetingtime.dto.CheckinRequestDTO;
import org.whu.fleetingtime.dto.CheckinResponseDTO;
import org.whu.fleetingtime.service.CheckinsService;

import java.util.Random;

@Service
public class CheckinsServiceImpl implements CheckinsService {

    private static final Random random = new Random();

    @Override
    public CheckinResponseDTO checkin(String token, CheckinRequestDTO request) {
        // 1. 解析 token 获取 userId（此处假装解析得到用户ID）
        Long userId = parseTokenToUserId(token); // TODO: 实际写法之后补上

        // 2. 模拟保存打卡记录，生成 checkinId
        Long checkinId = random.nextLong(100000L);

        // 3. 模拟是否是首次到达城市
        boolean isNewCity = random.nextBoolean();

        // 4. 构建响应
        return CheckinResponseDTO.builder()
                .checkinId(checkinId)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .city(request.getCity())
                .province(request.getProvince())
                .timestamp(request.getTimestamp())
                .note(request.getNote())
                .device(request.getDevice())
                .isNewCity(isNewCity)
                .highlightedCitiesCount(random.nextInt(100))
                .build();
    }

    private Long parseTokenToUserId(String token) {
        // 假装从 token 中解析出用户ID
        return 12L;
    }
}
