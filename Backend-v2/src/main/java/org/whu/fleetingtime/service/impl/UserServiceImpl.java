package org.whu.fleetingtime.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.user.*;
import org.whu.fleetingtime.entity.User;
import org.whu.fleetingtime.exception.BizException;
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
        // 模拟头像上传逻辑，这里建议你替换为真实的文件上传逻辑
        String fakeUrl = "https://cdn.example.com/avatar/" + avatarFile.getOriginalFilename();
        user.setAvatarUrl(fakeUrl);
        userRepository.save(user);
        return fakeUrl;
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
