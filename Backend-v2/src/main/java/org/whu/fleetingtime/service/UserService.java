package org.whu.fleetingtime.service;

import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.user.*;

public interface UserService {
    String register(UserRegisterRequestDTO userRegisterRequestDTO);
    String register_phone(PhoneRegisterRequestDTO phoneRegisterRequestDTO);
    String register_email(EmailRegisterRequestDTO emailRegisterRequestDTO);
    String login(LoginRequestDTO loginRequestDTO);

    UserUpdateResponseDTO updateUser(String userId, UserUpdateRequestDTO dto);
    String updateUserAvatar(String userId, MultipartFile avatarFile);
    UserInfoResponseDTO getUserInfoById(String userId);
    boolean deleteUserAndAllRelatedData(String userId);

}
