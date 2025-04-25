package org.whu.fleetingtime.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whu.fleetingtime.mapper.UserMapper;
import org.whu.fleetingtime.pojo.User;
import org.whu.fleetingtime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public User login(String username, String password) {
        logger.info("[UserServiceLogin]尝试登录，用户名，密码: {}, {}", username, password);
        User user = userMapper.selectByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            logger.info("[UserServiceLogin]登录成功");
            return user; // 登录成功
        }
        logger.info("[UserServiceLogin]登录失败");
        return null; // 登录失败
    }

    @Override
    public boolean register(User user) {
        logger.info("[UserServiceRegister]尝试注册，用户名，密码: {}, {}", user.getUsername(), user.getPassword());
        User existing = userMapper.selectByUsername(user.getUsername());
        if (existing != null) {
            logger.warn("[UserServiceRegister]注册失败，改用户名已存在: {}", user.getUsername());
            return false; // 用户名已存在
        }
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        int result = userMapper.insertUser(user);
        logger.info("[UserServiceRegister]注册成功，用户名，id: {}, {}", user.getUsername(), user.getPassword());
        return result == 1;
    }

    @Override
    public User findUserById(Long id) {
        logger.info("[UserServicefindUserById]根据id查询用户: {}", id);
        return userMapper.selectByUserId(id);
    }

    @Override
    public User findUserByUsername(String username) {
        logger.info("[UserServicefindUserById]根据username查询用户: {}", username);
        return userMapper.selectByUsername(username);
    }
}
