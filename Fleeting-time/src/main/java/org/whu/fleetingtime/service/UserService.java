package org.whu.fleetingtime.service;

import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.user.UserInfoResponseDTO;
import org.whu.fleetingtime.dto.user.UserUpdateResponseDTO;
import org.whu.fleetingtime.pojo.User;

public interface UserService {
    User login(String username, String password);
    boolean register(User user);
    User findUserById(Long id);
    User findUserByUsername(String username);
    UserUpdateResponseDTO updateUser(Long userId, String updatedUsername, String originPassword, String updatedPassword, MultipartFile avatarFile);
    UserInfoResponseDTO getUserInfoById(Long userId);
}
