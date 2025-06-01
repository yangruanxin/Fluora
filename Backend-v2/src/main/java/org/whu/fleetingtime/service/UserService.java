package org.whu.fleetingtime.service;

import org.whu.fleetingtime.dto.EmailRegisterRequestDTO;
import org.whu.fleetingtime.dto.PhoneRegisterRequestDTO;
import org.whu.fleetingtime.dto.UserRegisterRequestDTO;
import org.whu.fleetingtime.dto.LoginRequestDTO;

public interface UserService {
    String register(UserRegisterRequestDTO userRegisterRequestDTO);
    String register_phone(PhoneRegisterRequestDTO phoneRegisterRequestDTO);
    String register_email(EmailRegisterRequestDTO emailRegisterRequestDTO);
    String login(LoginRequestDTO loginRequestDTO);

}
