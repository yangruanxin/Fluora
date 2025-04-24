package org.example.fleetingtime.controller;


import org.example.fleetingtime.bean.User;
import org.example.fleetingtime.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.fleetingtime.service.UserService;

@RequestMapping("/api/v1" )
@CrossOrigin
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    //注册
    @PostMapping("/register")
    public R register(@RequestBody User user) {
        boolean result = userService.register(user);
        return result ? R.ok("注册成功") : R.error(501,"用户名已存在");
    }
    //登录
    @PostMapping("/login")
    public R login(@RequestBody User user) {
        boolean result = userService.login(user.getUsername(), user.getPassword());
        return result ? R.ok("登录成功") : R.error(502,"用户名或密码错误");
    }
    //注销
    @DeleteMapping("/logout")
    public R logout(@RequestBody User user) {
        boolean result = userService.logout(user);
        return result ? R.ok("注销成功") : R.error(503,"用户名不存在");
    }
    //new....
}
