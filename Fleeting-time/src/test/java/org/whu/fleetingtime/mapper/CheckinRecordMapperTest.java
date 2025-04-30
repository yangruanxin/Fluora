package org.whu.fleetingtime.mapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.whu.fleetingtime.pojo.CheckinRecord;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CheckinRecordMapperTest {

    @Autowired
    private TravelPostMapper checkinRecordMapper;

    // 用于后续测试的全局变量
    private static final Long testUserId = 999L;
    private static final String testCity = "测试市";

    @Test
    @Order(1)
    void testInsert() {
        CheckinRecord record = new CheckinRecord();
        record.setUserId(testUserId);
        record.setLatitude(30.12345);
        record.setLongitude(114.12345);
        record.setCity(testCity);
        record.setProvince("测试省");
        record.setTimestamp(LocalDateTime.now());
        record.setNote("测试打卡");
        record.setDevice("JUnitTest");
        record.setCreatedTime(LocalDateTime.now());
        record.setUpdatedTime(LocalDateTime.now());

        int result = checkinRecordMapper.insert(record);
        Long insertedId = record.getId();

        assertEquals(1, result);
        assertNotNull(insertedId);
    }

    @Test
    @Order(2)
    void testExistsByUserIdAndCity() {
        boolean exists = checkinRecordMapper.existsByUserIdAndCity(testUserId, testCity);
        assertTrue(exists);
    }

    @Test
    @Order(3)
    void testCountDistinctCitiesByUserId() {
        int count = checkinRecordMapper.countDistinctCitiesByUserId(testUserId);
        assertTrue(count >= 1);
    }

    @Test
    @Order(4)
    void testSelectByUserId() {
        List<CheckinRecord> records = checkinRecordMapper.selectByUserId(testUserId);
        assertNotNull(records);
        assertFalse(records.isEmpty());
        assertEquals(testCity, records.get(0).getCity());
    }
}
