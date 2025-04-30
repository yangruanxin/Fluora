package org.whu.fleetingtime.service;

import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.user.*;
import org.whu.fleetingtime.pojo.User;

public interface UserService {
    User login(UserLoginRequestDTO userLoginRequestDTO);
    boolean register(UserRegisterRequestDTO userRegisterRequestDTO);
    User findUserById(Long id);
    User findUserByUsername(String username);
    UserUpdateResponseDTO updateUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO);
    String updateUserAvatar(Long userId, MultipartFile file);
    UserInfoResponseDTO getUserInfoById(Long userId);
    void deleteUserAndAllRelatedData(Long userId);
}
