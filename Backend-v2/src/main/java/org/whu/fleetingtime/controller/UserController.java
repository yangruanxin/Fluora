package org.whu.fleetingtime.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.dto.user.*;
import org.whu.fleetingtime.service.UserService;

@RestController
@RequestMapping("api/user")
@Tag(name = "用户接口", description = "")
public class UserController {

    //private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("test")
    @Operation(summary = "hhhh", description = "hhh")
    public String register() {
        return "register";
    }

    // 注册接口
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterRequestDTO userRegisterRequestDTO) {
            String token =userService.register(userRegisterRequestDTO);
            return Result.success("注册成功",token);
    }
    // 注册接口
    @PostMapping("/register_phone")
    public Result<String> register_phone(@RequestBody PhoneRegisterRequestDTO phoneRegisterRequestDTO) {
        String token =userService.register_phone(phoneRegisterRequestDTO);
        return Result.success("注册成功",token);
    }
    // 注册接口
    @PostMapping("/register_email")
    public Result<String> register_email(@RequestBody EmailRegisterRequestDTO emailRegisterRequestDTO) {
        String token =userService.register_email(emailRegisterRequestDTO);
        return Result.success("注册成功",token);
    }
    // 登录接口
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String token =userService.login(loginRequestDTO);
        return Result.success("登录成功",token);
    }
    // 更新用户信息
    @PutMapping("/info")
    public Result<UserUpdateResponseDTO> updateUser(@RequestBody UserUpdateRequestDTO userUpdateRequestDTO,
                                                    HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        UserUpdateResponseDTO responseDTO = userService.updateUser(userId, userUpdateRequestDTO);
        return Result.success("用户信息已更新",responseDTO);
    }

    // 上传头像
    @Operation(summary = "上传用户头像", description = "通过 multipart/form-data 上传头像文件")
    @PutMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> updateUserAvatar(@Parameter(description = "用户头像", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                               @RequestParam MultipartFile avatarFile,HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        String newAvatarUrl = userService.updateUserAvatar(userId, avatarFile);
        return Result.success("头像上传成功", newAvatarUrl);
    }

    // 获取当前用户信息
    @GetMapping("/me")
    public Result<UserInfoResponseDTO> getMyInfo(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        UserInfoResponseDTO userInfo = userService.getUserInfoById(userId);
        return Result.success("用户当前信息",userInfo);
    }

    // 删除账号
    @DeleteMapping
    public Result<String> deleteUserAllData(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        userService.deleteUserAndAllRelatedData(userId);
        return Result.success("账号注销成功");
    }
}
