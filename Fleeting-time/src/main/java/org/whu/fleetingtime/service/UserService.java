package org.whu.fleetingtime.service;

import org.whu.fleetingtime.dto.user.*;
import org.whu.fleetingtime.pojo.User;

public interface UserService {
    User login(UserLoginRequestDTO userLoginRequestDTO);
    boolean register(UserRegisterRequestDTO userRegisterRequestDTO);
    User findUserById(Long id);
    User findUserByUsername(String username);
    UserUpdateResponseDTO updateUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO);
    UserInfoResponseDTO getUserInfoById(Long userId);
}
