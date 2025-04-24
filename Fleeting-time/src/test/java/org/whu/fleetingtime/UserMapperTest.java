package org.whu.fleetingtime;

import org.whu.fleetingtime.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.whu.fleetingtime.mapper.UserMapper;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("testuser12332123");
        user.setPassword("1234dsa56");
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        int result = userMapper.insertUser(user);

        assertEquals(1, result); // 确保插入成功
        assertNotNull(user.getId()); // 确保返回了自增主键

        System.out.println("插入成功，用户ID为：" + user.getId());
    }

    @Test
    public void testSelectByUsername() {
        User user = userMapper.selectByUsername("testuser123");
        assertNotNull(user);
        assertEquals("testuser123", user.getUsername());

        System.out.println("查询成功，密码为：" + user.getPassword());
    }

    @Test
    public void testDeleteByUserID() {
        User user = userMapper.selectByUsername("testuser123");
        assertNotNull(user);

        int result = userMapper.deleteByUserID(user.getId());
        assertEquals(1, result);

        User deleted = userMapper.selectByUsername("testuser123");
        assertNull(deleted);

        System.out.println("删除成功！");
    }
}
