package org.whu.fleetingtime.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.whu.fleetingtime.pojo.User;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsertUser() {
        try {
            User user = new User();
            user.setUsername(UUID.randomUUID().toString());
            user.setPassword("1234dsa56");
            user.setCreatedTime(LocalDateTime.now());
            user.setUpdatedTime(LocalDateTime.now());

            int result = userMapper.insertUser(user);
            assertEquals(1, result); // 确保插入成功
            assertNotNull(user.getId()); // 确保返回了自增主键

            System.out.println("插入成功，用户ID为：" + user.getId());
        } catch (Exception e) {
            System.err.println("testInsertUser 异常: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteByUserID() {
        try {
            User user = new User();
            user.setUsername("dnwjqidhbidwqosibhiwdbwdhbsadkwq_" + UUID.randomUUID());
            user.setPassword("1234dsa56");
            userMapper.insertUser(user);
            User selectedUser = userMapper.selectByUsername(user.getUsername());
            assertNotNull(selectedUser);

            int result = userMapper.deleteByUserId(selectedUser.getId());
            assertEquals(1, result);

            User deleted = userMapper.selectByUsername(user.getUsername());
            assertNull(deleted);

            System.out.println("删除成功！");
        } catch (Exception e) {
            System.err.println("testDeleteByUserID 异常: " + e.getMessage());
        }
    }
}
