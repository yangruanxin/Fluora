package org.whu.fleetingtime.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.user.*;
import org.whu.fleetingtime.exception.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.whu.fleetingtime.exception.BizExceptionEnum;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.pojo.User;
import org.whu.fleetingtime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.util.JwtUtils;

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

    // Token验证测试
    @GetMapping("/hello")
    public Result<String> secretHello(HttpServletRequest request) {
        // 从 request 获取 userId（由拦截器注入）
        String userId = (String) request.getAttribute("userId");
        logger.info("【hello测试token校验】拦截器已注入 userId: {}", userId);
        User user = userService.findUserById(Long.parseLong(userId));
        logger.info("【Token有效】用户ID: {}，用户名: {}", userId, user.getUsername());
        return Result.success("Hello, " + user.getUsername() + "!");
    }


    // 登录接口
    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        logger.info("【登录请求】用户名: {}", userLoginRequestDTO.getUsername());
        User loggedInUser = userService.login(userLoginRequestDTO);

        if (loggedInUser != null) {
            // 登录成功后返回JWT
            Long userId = userService.findUserByUsername(userLoginRequestDTO.getUsername()).getId();
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", userId.toString());
            String token = JwtUtils.createJwt(secretKey, duration * 60L * 1000L, claims);
            logger.info("【登录成功】用户ID: {}，Token: {}", userId, token);
            UserLoginResponseDTO response = UserLoginResponseDTO.builder()
                    .token(token)
                    .build();
            return Result.success("login successful", response);
        } else {
            throw new BizException(BizExceptionEnum.USER_PASSWORD_OR_USERNAME_ERROR);
            //return Result.failure("invalid username or password");
        }
    }

    // 注册接口
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterRequestDTO userRegisterRequestDTO) {
        logger.info("【注册请求】用户名: {}", userRegisterRequestDTO.getUsername());
        boolean success = userService.register(userRegisterRequestDTO);
        if (success) {
            logger.info("【注册成功】用户名: {}", userRegisterRequestDTO.getUsername());
            return Result.success("registration successful");
        } else {
            logger.warn("【注册失败】用户名已存在: {}", userRegisterRequestDTO.getUsername());
            throw new BizException(BizExceptionEnum.USERNAME_ALREADY_EXISTS);
            //return Result.failure("username already exists");
        }
    }

    // 更新用户信息接口
    @PutMapping("/info")
    public Result<UserUpdateResponseDTO> updateUser(@RequestBody UserUpdateRequestDTO userUpdateRequestDTO,
                                                    HttpServletRequest request) {
        // 通过 token 拿到 userId
        Long userId = Long.parseLong((String) request.getAttribute("userId"));
        logger.info("【用户信息更新请求】用户id: {}, 请求: {}", userId, userUpdateRequestDTO);
        try {
            UserUpdateResponseDTO responseDTO = userService.updateUser(userId, userUpdateRequestDTO);
            logger.info("【用户信息更新成功】用户id: {}", userId);
            return Result.success(responseDTO);
        } catch (BizException e) {
            logger.warn("【用户信息更新失败】用户id: {}, 原因: {}", userId, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/avatar")
    public Result<String> updateUserAvatar(@RequestParam MultipartFile avatarFile, HttpServletRequest request) {
            Long userId = Long.parseLong((String) request.getAttribute("userId"));
            logger.info("【头像上传请求】用户id: {}", userId);
            String newAvatarUrl = userService.updateUserAvatar(userId, avatarFile);
            return Result.success("success", newAvatarUrl);
    }

    // 已经登录的用户查询自己的个人信息
    @GetMapping("/me")
    public Result<UserInfoResponseDTO> getMyInfo(HttpServletRequest request) {
        // 从 request 获取 userId（由拦截器注入）
        Long userId = Long.parseLong((String) request.getAttribute("userId"));
        logger.info("【用户个人信息查询】用户id: {}", userId);
        UserInfoResponseDTO userInfo = userService.getUserInfoById(userId);
        logger.info("【用户信息查询成功】用户id: {}", userId);
        return Result.success(userInfo);
    }

    // 注销删除账号
    @DeleteMapping
    public Result<String> deleteUserAllData(HttpServletRequest request){
        // 从 request 获取 userId（由拦截器注入）
        Long userId = Long.parseLong((String) request.getAttribute("userId"));
        logger.info("【用户删除账号】用户id: {}", userId);
        userService.deleteUserAndAllRelatedData(userId);
        logger.info("【账号删除成功】用户id: {}", userId);
        return Result.success("delete successful");
    }
}
