package org.whu.fleetingtime.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whu.fleetingtime.exception.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.whu.fleetingtime.exception.BizExceptionEnum;
import org.whu.fleetingtime.pojo.Result;
import org.whu.fleetingtime.pojo.User;
import org.whu.fleetingtime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.duration}")
    private int duration; // minutes

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // helloTest
    @GetMapping("/hello")
    public Result<String> secretHello(@RequestHeader("Authorization") String token) {
        // 从 token 中解析用户名
        logger.info("【Token校验】收到请求，Token: {}", token);
        String userId = (String) JwtUtil.parseJWT(secretKey, token).get("id");
        User user = userService.findUserById(Long.parseLong(userId));
        logger.info("【Token有效】用户ID: {}，用户名: {}", userId, user.getUsername());
        return Result.success("Hello, " + user.getUsername() + "!");
    }

    // 登录接口
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        logger.info("【登录请求】用户名: {}", user.getUsername());
        User loggedInUser = userService.login(user.getUsername(), user.getPassword());
        if (loggedInUser != null) {
            // 登录成功后返回JWT

            Long userId = userService.findUserByUsername(user.getUsername()).getId();
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", userId.toString());
            String token = JwtUtil.createJwt(secretKey, duration * 60L * 1000L, claims); // 有效期30分钟
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            logger.info("【登录成功】用户ID: {}，Token: {}", userId, token);
            return Result.success("login successful", result);
        } else {
            throw new BizException(BizExceptionEnum.USER_PASSWORD_OR_USERNAME_ERROR);
            //return Result.failure("invalid username or password");
        }
    }

    // 注册接口
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        logger.info("【注册请求】用户名: {}", user.getUsername());
        boolean success = userService.register(user);
        if (success) {
            logger.info("【注册成功】用户名: {}", user.getUsername());
            return Result.success("registration successful");
        } else {
            logger.warn("【注册失败】用户名已存在: {}", user.getUsername());
            throw new BizException(BizExceptionEnum.USERNAME_ALREADY_EXISTS);
            //return Result.failure("username already exists");
        }
    }
}
