package org.whu.fleetingtime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.whu.fleetingtime.dto.EmailRegisterRequestDTO;
import org.whu.fleetingtime.dto.LoginRequestDTO;
import org.whu.fleetingtime.dto.PhoneRegisterRequestDTO;
import org.whu.fleetingtime.dto.UserRegisterRequestDTO;
import org.whu.fleetingtime.entity.User;
import org.whu.fleetingtime.repository.UserRepository;
import org.whu.fleetingtime.service.UserService;
import org.whu.fleetingtime.util.JwtUtil;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String generateRandomUsername() {
        String username=null;
        do {
            int randomNum = (int)(Math.random() * 900000) + 100000;
            username = "用户" + randomNum;
        } while (userRepository.existsByUsername(username) == true);
        return username;
    }

    @Override
    @Transactional
    public String register(UserRegisterRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        //哈希加盐处理
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return jwtUtil.generateToken(user.getId());
    }
    @Override
    public String register_phone(PhoneRegisterRequestDTO dto) {
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("用户已存在,手机号已被注册");
        }
        User user = new User();
        user.setUsername(generateRandomUsername());
        user.setPhone(dto.getPhone());
        //哈希加盐处理
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return jwtUtil.generateToken(user.getId());
    }
    @Override
    public String register_email(EmailRegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("用户已存在,邮箱已被注册");
        }
        User user = new User();
        user.setUsername(generateRandomUsername());
        user.setEmail(dto.getEmail());
        //哈希加盐处理
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return jwtUtil.generateToken(user.getId());
    }
    @Override
    public String login(LoginRequestDTO dto) {
        User user = null;
        if (dto.getIdentifier().matches("^\\d{11}$")) {
            // 手机号
            user = userRepository.findByPhone(dto.getIdentifier());
        } else if (dto.getIdentifier().matches("^[\\w.%+-]+@[\\w.-]+\\.\\w{2,}$")) {
            // 邮箱
            user = userRepository.findByEmail(dto.getIdentifier());
        } else {
            // 用户名
            user = userRepository.findByUsername(dto.getIdentifier());
        }
        if (user == null) {
            //throw new BizException(BizExceptionEnum.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            //throw new BizException(BizExceptionEnum.PASSWORD_ERROR);
        }
        return jwtUtil.generateToken(user.getId());
    }
}
