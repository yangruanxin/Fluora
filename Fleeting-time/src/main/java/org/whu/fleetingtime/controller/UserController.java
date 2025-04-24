package org.whu.fleetingtime.controller;

import org.springframework.beans.factory.annotation.Value;
import org.whu.fleetingtime.pojo.User;
import org.whu.fleetingtime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private UserService userService;

    // helloTest
    @GetMapping("/hello")
    public String secretHello(@RequestHeader("Authorization") String token) {
        // 从 token 中解析用户名
        String userId = (String) JwtUtil.parseJWT(secretKey, token).get("id");
        User user = userService.findUserById(Long.parseLong(userId));
        return "Hello, " + user.getUsername() + "!";
    }

    // 登录接口
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User loggedInUser = userService.login(user.getUsername(), user.getPassword());
        if (loggedInUser != null) {
            // 登录成功后返回JWT
            Long userId = userService.findUserByUsername(user.getUsername()).getId();
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", userId.toString());
            String token = JwtUtil.createJwt(secretKey, 30 * 60 * 1000, claims); // 有效期30分钟
            return "登录成功，JWT令牌: " + token;
        } else {
            return "登录失败，用户名或密码错误。";
        }
    }

    // 注册接口
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            return "注册成功！";
        } else {
            return "注册失败，用户名已存在。";
        }
    }
}
