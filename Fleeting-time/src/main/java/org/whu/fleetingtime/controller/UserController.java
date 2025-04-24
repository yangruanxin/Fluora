package org.whu.fleetingtime.controller;

import org.springframework.beans.factory.annotation.Value;
import org.whu.fleetingtime.pojo.Result;
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
    public Result<String> secretHello(@RequestHeader("Authorization") String token) {
        // 从 token 中解析用户名
        String userId = (String) JwtUtil.parseJWT(secretKey, token).get("id");
        User user = userService.findUserById(Long.parseLong(userId));
        return Result.success("Hello, " + user.getUsername() + "!");
    }

    // 登录接口
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        User loggedInUser = userService.login(user.getUsername(), user.getPassword());
        if (loggedInUser != null) {
            // 登录成功后返回JWT
            Long userId = userService.findUserByUsername(user.getUsername()).getId();
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", userId.toString());
            String token = JwtUtil.createJwt(secretKey, 30 * 60 * 1000, claims); // 有效期30分钟
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            return Result.success("登录成功", result);
        } else {
            return Result.failure("登录失败，用户名或密码错误。");
        }
    }

    // 注册接口
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功！", null);
        } else {
            return Result.failure("注册失败，用户名已存在。");
        }
    }
}
