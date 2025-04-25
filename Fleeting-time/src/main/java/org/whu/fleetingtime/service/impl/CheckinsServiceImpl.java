package org.whu.fleetingtime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.whu.fleetingtime.dto.CheckinRequestDTO;
import org.whu.fleetingtime.dto.CheckinResponseDTO;
import org.whu.fleetingtime.exception.BizException;
import org.whu.fleetingtime.exception.BizExceptionEnum;
import org.whu.fleetingtime.mapper.CheckinRecordMapper;
import org.whu.fleetingtime.pojo.CheckinRecord;
import org.whu.fleetingtime.service.CheckinsService;
import org.whu.fleetingtime.util.JwtUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class CheckinsServiceImpl implements CheckinsService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    CheckinRecordMapper checkinRecordMapper;

    @Override
    public CheckinResponseDTO checkin(String token, CheckinRequestDTO request) {
        // 0. 输入校验
        if (request.getLatitude() == null ||
                request.getLongitude() == null ||
                request.getCity() == null ||
                request.getTimestamp() == null) {
            throw new BizException(BizExceptionEnum.INVALID_CHECKIN_PARAMETER);
        }

        // 1. 解析 token 获取 userId
        String userIdStr = (String) JwtUtil.parseJWT(secretKey, token).get("id");
        Long userId = Long.parseLong(userIdStr);

        // 2. 查询该城市是否第一次打卡
        boolean isNewCity = !checkinRecordMapper.existsByUserIdAndCity(userId, request.getCity());

        // 3. 获取当前时间作为 createdTime 和 updatedTime
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 4. 构造打卡记录对象
        CheckinRecord record = new CheckinRecord();
        record.setUserId(userId);
        record.setLatitude(request.getLatitude());
        record.setLongitude(request.getLongitude());
        record.setCity(request.getCity());
        record.setProvince(request.getProvince());
        record.setTimestamp(LocalDateTime.parse(request.getTimestamp(), fmt));
        record.setNote(request.getNote());
        record.setDevice(request.getDevice());
        record.setCreatedTime(now);
        record.setUpdatedTime(now);

        // 5. 存入数据库
        checkinRecordMapper.insert(record);

        // 6. 查询用户的高亮城市数量
        int highlightedCitiesCount = checkinRecordMapper.countDistinctCitiesByUserId(userId);

        // 7. 构建响应
        return CheckinResponseDTO.builder()
                .checkinId(record.getId())  // 注意是插入后自动生成的主键
                .latitude(record.getLatitude())
                .longitude(record.getLongitude())
                .city(record.getCity())
                .province(record.getProvince())
                .timestamp(record.getTimestamp())
                .note(record.getNote())
                .device(record.getDevice())
                .isNewCity(isNewCity)
                .highlightedCitiesCount(highlightedCitiesCount)
                .build();
    }
}
