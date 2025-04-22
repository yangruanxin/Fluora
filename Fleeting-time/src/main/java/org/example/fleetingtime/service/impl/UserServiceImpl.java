package org.example.test.service.impl;

import org.example.test.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.test.repository.UserRepository;
import org.example.test.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean register(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return false; // 用户名已存在
        }
        userRepository.save(user);
        return true;
    }

    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    @Override
    public boolean logout(User user) {
        if (userRepository.findByUsername(user.getUsername()) == null) {
            return false; // 用户名已不存在
        }
        userRepository.delete(user);
        return true;
    }
}
