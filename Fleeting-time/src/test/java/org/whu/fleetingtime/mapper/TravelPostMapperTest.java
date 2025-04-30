package org.whu.fleetingtime.mapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.whu.fleetingtime.pojo.TravelPost;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TravelPostMapperTest {

    @Autowired
    private TravelPostMapper checkinRecordMapper;

    // 用于后续测试的全局变量
//    private static final Long testUserId = 999L;
//    private static final String testCity = "测试市";

    @Test
    @Order(1)
    void testInsert() {
        TravelPost travelPost = new TravelPost();
        travelPost.setUserId(13L);
        travelPost.setTitle("哈哈哈");
        travelPost.setContent("哈哈哈哈哈哈哈哈哈哈哈");
        travelPost.setLocationName("江上");
        travelPost.setLatitude(30.12345);
        travelPost.setLongitude(114.12345);
        travelPost.setBeginTime(LocalDateTime.now());
        travelPost.setEndTime(LocalDateTime.now());
        travelPost.setCreatedTime(LocalDateTime.now());
        travelPost.setUpdatedTime(LocalDateTime.now());

        int result = checkinRecordMapper.insert(travelPost);
        Long insertedId = travelPost.getId();

        assertEquals(1, result);
        assertNotNull(insertedId);
    }

//    @Test
//    @Order(2)
//    void testExistsByUserIdAndCity() {
//        boolean exists = checkinRecordMapper.existsByUserIdAndCity(testUserId, testCity);
//        assertTrue(exists);
//    }
//
//    @Test
//    @Order(3)
//    void testCountDistinctCitiesByUserId() {
//        int count = checkinRecordMapper.countDistinctCitiesByUserId(testUserId);
//        assertTrue(count >= 1);
//    }
//
//    @Test
//    @Order(4)
//    void testSelectByUserId() {
//        List<TravelPost> records = checkinRecordMapper.selectByUserId(testUserId);
//        assertNotNull(records);
//        assertFalse(records.isEmpty());
//        assertEquals(testCity, records.get(0).getCity());
//    }
}
