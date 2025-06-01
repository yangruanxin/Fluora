package org.whu.fleetingtime.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.user.*;
import org.whu.fleetingtime.entity.User;
import org.whu.fleetingtime.exception.BizException;
import org.whu.fleetingtime.repository.UserRepository;
import org.whu.fleetingtime.service.UserService;
import org.whu.fleetingtime.util.JwtUtil;
import org.whu.fleetingtime.util.AliyunOssUtil;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

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
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        //哈希加盐处理
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return jwtUtil.generateToken(user.getId());
    }
    @Override
    @Transactional
    public String register_phone(PhoneRegisterRequestDTO dto) {
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new BizException("用户已存在,手机号已被注册");
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
    @Transactional
    public String register_email(EmailRegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BizException("用户已存在,邮箱已被注册");
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
            throw new BizException("用户名错误");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException("密码错误");
        }
        return jwtUtil.generateToken(user.getId());
    }
    @Override
    @Transactional
    public UserUpdateResponseDTO updateUser(String userId, UserUpdateRequestDTO dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BizException("用户不存在"));
        BeanUtils.copyProperties(dto, user);
        User updatedUser = userRepository.save(user);

        UserUpdateResponseDTO responseDTO = new UserUpdateResponseDTO();
        BeanUtils.copyProperties(updatedUser, responseDTO);
        return responseDTO;
    }

    @Override
    @Transactional
    public String updateUserAvatar(String userId, MultipartFile avatarFile) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BizException("用户不存在"));
        if (avatarFile == null || avatarFile.isEmpty()) {
            throw new BizException("文件不存在");
        }
        try {
            String suffix = StringUtils.getFilenameExtension(avatarFile.getOriginalFilename());
            String newAvatarUrl = "user/" + userId + "/" + UUID.randomUUID() + "." + suffix;
            InputStream inputStream = avatarFile.getInputStream();
            AliyunOssUtil.upload(newAvatarUrl, inputStream);

            // 删除旧头像
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                String oldObjectName = AliyunOssUtil.extractObjectNameFromUrl(user.getAvatarUrl());
                AliyunOssUtil.delete(oldObjectName);
            }

            // 更新用户头像地址及更新时间
            user.setAvatarUrl(newAvatarUrl);
            user.setUpdatedTime(LocalDateTime.now());
            userRepository.save(user);  // JPA的保存/更新

            return newAvatarUrl;
        } catch (IOException e) {
            throw new BizException("文件上传失败");
        }
    }

    @Override
    public UserInfoResponseDTO getUserInfoById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BizException("用户不存在"));
        UserInfoResponseDTO dto = new UserInfoResponseDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    @Override
    @Transactional
    public boolean deleteUserAndAllRelatedData(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BizException("用户不存在"));
        userRepository.delete(user);
        // 可扩展删除其他相关数据，如帖子、评论、好友等
        return true;
    }
}
