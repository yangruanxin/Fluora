package org.whu.fleetingtime.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.whu.fleetingtime.pojo.TravelPostImage;

@SpringBootTest
public class TravelPostImageMapperTest {
    @Autowired
    private TravelPostImageMapper travelPostImageMapper;

    @Test
    void testInsert() {
        TravelPostImage img1 = new TravelPostImage();
        img1.setImageUrl("111.com");
        img1.setPostId(12L);
        img1.setSortOrder(1);

        TravelPostImage img2 = new TravelPostImage();
        img2.setImageUrl("123.com");
        img2.setPostId(12L);
        img2.setSortOrder(2);

        int result1 = travelPostImageMapper.insert(img1);
        int result2 = travelPostImageMapper.insert(img2);
        Long insertedId1 = img1.getId();
        Long insertedId2 = img2.getId();

        System.out.println(insertedId2);
    }
}
