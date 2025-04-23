package org.example.fleetingtime.controller;


import org.example.fleetingtime.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.fleetingtime.service.UserService;


@RestController
public class UserController {
//    @RequestMapping(value="/register",consumes ={"application/json"})
//    public String register(){
//
//        return "register";
//    }
    @Autowired
    private UserService userService;
    //注册
    @RequestMapping(method=RequestMethod.POST,value="/register")
    public String register(@RequestBody User user) {
        boolean result = userService.register(user);
        return result ? "注册成功" : "用户名已存在";
    }
    //登录
    @RequestMapping(method=RequestMethod.POST,value="/login")
    public String login(@RequestBody User user) {
        boolean result = userService.login(user.getUsername(), user.getPassword());
        return result ? "登录成功" : "用户名或密码错误";
    }
    //注销
    @RequestMapping(method=RequestMethod.DELETE,value="/delete")
    public String logout(@RequestBody User user) {
        boolean result = userService.deactiveAccount(user);
        return result ? "注销成功" : "用户名不存在";
    }

}
