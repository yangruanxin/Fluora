package org.whu.fleetingtime.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.user.*;
import org.whu.fleetingtime.exception.BizException;
import org.whu.fleetingtime.exception.BizExceptionEnum;
import org.whu.fleetingtime.mapper.UserMapper;
import org.whu.fleetingtime.pojo.User;
import org.whu.fleetingtime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.whu.fleetingtime.util.AliyunOssUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public User login(UserLoginRequestDTO userLoginRequestDTO) {
        logger.info("[UserServiceLogin]尝试登录，用户名，密码: {}, {}", userLoginRequestDTO.getUsername(), userLoginRequestDTO.getPassword());
        User user = userMapper.selectByUsername(userLoginRequestDTO.getUsername());
        if (user != null && user.getPassword().equals(userLoginRequestDTO.getPassword())) {
            logger.info("[UserServiceLogin]登录成功");
            return user; // 登录成功
        }
        logger.info("[UserServiceLogin]登录失败");
        return null; // 登录失败
    }

    @Override
    public boolean register(UserRegisterRequestDTO userRegisterRequestDTO) {
        logger.info("[UserServiceRegister]尝试注册，用户名，密码: {}, {}", userRegisterRequestDTO.getUsername(), userRegisterRequestDTO.getPassword());
        User existing = userMapper.selectByUsername(userRegisterRequestDTO.getUsername());
        if (existing != null) {
            logger.warn("[UserServiceRegister]注册失败，改用户名已存在: {}", userRegisterRequestDTO.getUsername());
            return false; // 用户名已存在
        }
        User user = new User();
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        user.setUsername(userRegisterRequestDTO.getUsername());
        user.setPassword(userRegisterRequestDTO.getPassword());
        int result = userMapper.insertUser(user);
        logger.info("[UserServiceRegister]注册成功，用户名，id: {}, {}", user.getUsername(), user.getPassword());
        return result == 1;
    }

    @Override
    public User findUserById(Long id) {
        logger.info("[UserServiceFindUserById]根据id查询用户: {}", id);
        return userMapper.selectByUserId(id);
    }

    @Override
    public User findUserByUsername(String username) {
        logger.info("[UserServiceFindUserById]根据username查询用户: {}", username);
        return userMapper.selectByUsername(username);
    }

    @Override
    public UserUpdateResponseDTO updateUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        String updatedUsername = userUpdateRequestDTO.getUsername();
        String originPassword = userUpdateRequestDTO.getOriginPassword();
        String updatedPassword = userUpdateRequestDTO.getPassword();
        MultipartFile avatarFile = userUpdateRequestDTO.getAvatar();
        logger.info("[UserServiceUpdate]开始更新用户信息，用户id: {}", userId);

        // 返回字段
        String newUsername = null;
        String newAvatarUrl = null;
        boolean isPasswordUpdated = false;

        // 参数全空
        if ((updatedUsername==null || updatedUsername.isEmpty())
                &&(updatedPassword==null || updatedPassword.isEmpty())
                &&(avatarFile==null || avatarFile.isEmpty())) {
            logger.warn("[UserServiceUpdate]修改参数不能全空，用户id: {}", userId);
            throw new BizException(BizExceptionEnum.ALL_NULL_PARAMETERS);
        }

        User user = userMapper.selectByUserId(userId);
        User updatedUser = new User();
        if (user == null) {
            logger.warn("[UserServiceUpdate]用户不存在，用户id: {}", userId);
            throw new BizException(BizExceptionEnum.USER_NOT_FOUND);
        }
        updatedUser.setId(userId);

        // 修改密码前校验原始密码
        if (updatedPassword != null && !updatedPassword.isEmpty()) {
            if (originPassword == null || !originPassword.equals(user.getPassword())) {
                logger.warn("[UserServiceUpdate]原始密码不正确，用户id: {}", userId);
                throw new BizException(BizExceptionEnum.PASSWORD_INCORRECT);
            }
            if(originPassword.equals(updatedPassword)){
                logger.warn("[UserServiceUpdate]新旧密码不能一样，用户id: {}", userId);
                throw new BizException(BizExceptionEnum.SAME_PASSWORD);
            }
            updatedUser.setPassword(updatedPassword);
            isPasswordUpdated = true;
            logger.info("[UserServiceUpdate]密码更新成功，用户id: {}", userId);
        }

        // 用户名不能重复
        if (updatedUsername != null && !updatedUsername.isEmpty()) {
            User find = userMapper.selectByUsername(updatedUsername);
            if (find != null && !find.getUsername().equals(updatedUsername)) {
                logger.warn("[UserServiceUpdate]用户名已存在，用户id: {}, 新用户名: {}", userId, updatedUsername);
                throw new BizException(BizExceptionEnum.USERNAME_ALREADY_EXISTS);
            }
            newUsername = updatedUsername;
            updatedUser.setUsername(updatedUsername);
            logger.info("[UserServiceUpdate]用户名更新成功，用户id: {}, 新用户名: {}", userId, updatedUsername);
        }

        // 处理头像上传
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String suffix = StringUtils.getFilenameExtension(avatarFile.getOriginalFilename());
                String objectName = "user/" + userId + "/" + UUID.randomUUID() + "." + suffix;
                InputStream inputStream = avatarFile.getInputStream();
                String url = AliyunOssUtils.upload(objectName, inputStream);
                updatedUser.setAvatarUrl(url);
                newAvatarUrl = url;
                logger.info("[UserServiceUpdate]头像上传成功，用户id: {}, 头像URL: {}", userId, url);
            } catch (IOException e) {
                logger.error("[UserServiceUpdate]头像上传失败，用户id: {}", userId, e);
                throw new BizException(BizExceptionEnum.FILE_UPLOAD_FAILED);
            }
        }

        // 删除旧头像
        if (user.getAvatarUrl() != null) {
            String oldAvatarUrl = user.getAvatarUrl();
            String oldObjectName = AliyunOssUtils.extractObjectNameFromUrl(oldAvatarUrl);
            AliyunOssUtils.delete(oldObjectName);
            logger.info("[UserServiceUpdate]旧头像删除成功，用户id: {}, 旧头像URL: {}", userId, oldAvatarUrl);
        }

        updatedUser.setUpdatedTime(LocalDateTime.now());
        userMapper.update(updatedUser);
        logger.info("[UserServiceUpdate]用户信息更新完成，用户id: {}", userId);

        return UserUpdateResponseDTO.builder()
                .username(newUsername)
                .avatarUrl(newAvatarUrl)
                .hasPasswordUpdated(isPasswordUpdated)
                .updatedTime(updatedUser.getUpdatedTime())
                .build();
    }

    @Override
    public UserInfoResponseDTO getUserInfoById(Long userId) {
        logger.info("[UserGetUserInfoById]用户信息查询，用户id: {}", userId);
        User user = userMapper.selectByUserId(userId);
        if (user == null){
            throw new BizException(BizExceptionEnum.USER_NOT_FOUND);
        }
        logger.info("[UserGetUserInfoById]用户信息查询成功，用户id: {}", userId);
        return UserInfoResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .createdTime(user.getCreatedTime())
                .updatedTime(user.getUpdatedTime())
                .build();
    }
}
