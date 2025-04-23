package org.example.fleetingtime.service.impl;

import org.example.fleetingtime.bean.User;
import org.example.fleetingtime.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.fleetingtime.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    public boolean register(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return false; // 用户名已存在
        }
        userMapper.save(user);
        return true;
    }

    public boolean login(String username, String password) {
        User user = userMapper.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    @Override
    public boolean deactiveAccount(User user) {
        User user1 = userMapper.findByUsername(user.getUsername());
        if (user1 == null) {
            return false; // 用户名已不存在
        }
        userMapper.delete(user1.getId());
        return true;
    }
}
