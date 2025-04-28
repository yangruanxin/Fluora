package org.whu.fleetingtime.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.whu.fleetingtime.dto.checkin.CheckinRequestDTO;
import org.whu.fleetingtime.dto.checkin.CheckinResponseDTO;
import org.whu.fleetingtime.exception.BizException;
import org.whu.fleetingtime.exception.BizExceptionEnum;
import org.whu.fleetingtime.mapper.CheckinRecordMapper;
import org.whu.fleetingtime.pojo.CheckinRecord;
import org.whu.fleetingtime.service.CheckinService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CheckinsServiceImpl implements CheckinService {

    private static final Logger logger = LoggerFactory.getLogger(CheckinsServiceImpl.class);

    @Autowired
    CheckinRecordMapper checkinRecordMapper;

    @Override
    public CheckinResponseDTO checkin(Long userId, CheckinRequestDTO request) {
        logger.info("[CheckinsServiceCheckin]收到打卡请求");
        // 0. 输入校验
        if (request.getLatitude() == null ||
                request.getLongitude() == null ||
                request.getCity() == null ||
                request.getTimestamp() == null) {
            logger.warn("[CheckinsServiceCheckin]请求参数无效");
            throw new BizException(BizExceptionEnum.INVALID_CHECKIN_PARAMETER);
        }

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
        logger.info("[CheckinsServiceCheckin]打卡请求已完成");

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
