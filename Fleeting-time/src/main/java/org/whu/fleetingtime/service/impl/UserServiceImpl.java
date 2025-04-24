package org.whu.fleetingtime.service.impl;

import org.whu.fleetingtime.mapper.UserMapper;
import org.whu.fleetingtime.pojo.User;
import org.whu.fleetingtime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user; // 登录成功
        }
        return null; // 登录失败
    }

    @Override
    public boolean register(User user) {
        User existing = userMapper.selectByUsername(user.getUsername());
        if (existing != null) {
            return false; // 用户名已存在
        }
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());
        int result = userMapper.insertUser(user);
        return result == 1;
    }

    @Override
    public User findUserById(Long id) {
        return userMapper.selectByUserId(id);
    }

    @Override
    public User findUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}
