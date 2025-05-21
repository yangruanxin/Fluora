//package org.whu.fleetingtime.service;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.whu.fleetingtime.pojo.User;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class UserServiceTest {
//
//    @Autowired
//    private UserService userService;
//
//    @Test
//    public void testRegisterAndLogin() {
//        try {
//            String username = "jwt_user_" + System.currentTimeMillis(); // 避免用户名冲突
//            User user = new User();
//            user.setUsername(username);
//            user.setPassword("nopass");
//
//            boolean registerResult = userService.register(user);
//            System.out.println("注册结果：" + registerResult);
//
//            User loginUser = userService.login(username, "nopass");
//            assertNotNull(loginUser);
//            assertEquals(username, loginUser.getUsername());
//
//            System.out.println("Service 登录测试成功");
//
//        } catch (Exception e) {
//            System.err.println("testRegisterAndLogin 异常: " + e.getMessage());
//        }
//    }
//}
