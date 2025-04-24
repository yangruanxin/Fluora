package org.whu.fleetingtime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.whu.fleetingtime.pojo.User;
import org.whu.fleetingtime.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testRegisterAndLogin() {
        // 创建新用户
        User user = new User();
        user.setUsername("jwt_user");
        user.setPassword("nopass");

        // 测试注册
        boolean registerResult = userService.register(user);
        System.out.println(registerResult);

        // 测试登录
        User loginUser = userService.login("jwt_user", "nopass");
        assertNotNull(loginUser);
        assertEquals("jwt_user", loginUser.getUsername());

        System.out.println("Service 登录测试成功");
    }

    @Test
    public void testWebhook() {
        System.out.println("测试流水线触发");
    }
}
