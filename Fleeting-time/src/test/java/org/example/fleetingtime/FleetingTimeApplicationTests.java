package org.example.fleetingtime;

import org.example.fleetingtime.bean.User;
import org.example.fleetingtime.mapper.UserMapper;
import org.example.fleetingtime.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FleetingTimeApplicationTests {

    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;

    @Test
    void testInsertUser() {
        // 1. 准备测试数据
        User user = new User();
        user.setUsername("testUser02");
        user.setPassword("testPswd02");
        // 2. 执行插入操作
        userService.register(user);
    }

    @Test
    void testSelectByNameXML(){
        // 1. prepare String name
        String name = "testUser";
        // 2. find
        User user = userMapper.findByUsername(name);
        System.out.println(user);
    }

    @Test
    void testSaveXML(){
        User user = new User();
        user.setUsername("testUserXML");
        user.setPassword("testPswdXML");
        userMapper.save(user);
    }

    @Test
    void testDeleteXML(){
        int id = 4;
        userMapper.delete(id);
    }
}
