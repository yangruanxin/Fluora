package org.whu.fleetingtime.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    private static final long EXPIRE_TIME = 1000 * 60 * 5; //  5 分钟

    private String generateRandomAvatar() {
        Random random = new Random();
        Integer number = random.nextInt(3) + 1; // 生成 0~2，再加 1 得 1~3
        return "avatar/defaults/d" + number.toString() + ".gif";
    }

    public String generateRandomUsername() {
        String username = null;
        do {
            int randomNum = (int) (Math.random() * 900000) + 100000;
            username = "用户" + randomNum;
        } while (userRepository.existsByUsername(username) == true);
        return username;
    }

    @Override
    @Transactional
    public String register(UserRegisterRequestDTO dto) {
        validateUserRegisterDTO(dto);
        try {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new BizException("用户名已存在");
            }
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setAvatarUrl(generateRandomAvatar());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            userRepository.save(user);
            return jwtUtil.generateToken(user.getId());
        } catch (DataAccessException e) {
            log.error("数据库异常", e);
            throw new BizException("服务器异常，请稍后重试");
        }
    }

    @Override
    @Transactional
    public String register_phone(PhoneRegisterRequestDTO dto) {
        validatePhoneRegisterDTO(dto);
        try {
            if (userRepository.existsByPhone(dto.getPhone())) {
                throw new BizException("手机号已被注册");
            }
            String code = redisTemplate.opsForValue().get("sms:" + dto.getPhone());
            if (code == null) {
                throw new BizException("验证码已过期");
            }
            if (!dto.getCode().equals(code)) {
                throw new BizException("验证码错误");
            }
            User user = new User();
            user.setUsername(generateRandomUsername());
            user.setPhone(dto.getPhone());
            user.setAvatarUrl(generateRandomAvatar());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            userRepository.save(user);
            return jwtUtil.generateToken(user.getId());
        } catch (DataAccessException e) {
            log.error("数据库异常", e);
            throw new BizException("服务器异常，请稍后重试");
        } catch (Exception e) {
            log.error("验证码服务异常", e);
            throw new BizException("验证码服务异常，请稍后重试");
        }
    }

    @Override
    @Transactional
    public String register_email(EmailRegisterRequestDTO dto) {
        validateEmailRegisterDTO(dto);
        try {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new BizException("邮箱已被注册");
            }
            String code = redisTemplate.opsForValue().get("email:" + dto.getEmail());
            if (code == null) {
                throw new BizException("验证码已过期");
            }
            if (!dto.getCode().equals(code)) {
                throw new BizException("验证码错误");
            }
            User user = new User();
            user.setUsername(generateRandomUsername());
            user.setEmail(dto.getEmail());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setAvatarUrl(generateRandomAvatar());
            userRepository.save(user);
            return jwtUtil.generateToken(user.getId());
        } catch (DataAccessException e) {
            log.error("数据库异常", e);
            throw new BizException("服务器异常，请稍后重试");
        } catch (Exception e) {
            log.error("验证码服务异常", e);
            throw new BizException("验证码服务异常，请稍后重试");
        }
    }

    @Override
    public String login(LoginRequestDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getIdentifier()) || !StringUtils.hasText(dto.getPassword())) {
            throw new BizException("用户名/邮箱/手机号和密码不能为空");
        }

        Optional<User> user = Optional.empty();
        try {
            if (dto.getIdentifier().matches("^\\d{11}$")) {
                user = userRepository.findByPhone(dto.getIdentifier());

            } else if (dto.getIdentifier().matches("^[\\w.%+-]+@[\\w.-]+\\.\\w{2,}$")) {
                user = userRepository.findByEmail(dto.getIdentifier());
            } else {
                user = Optional.ofNullable(userRepository.findByUsername(dto.getIdentifier()));
            }
        } catch (DataAccessException e) {
            log.error("数据库异常", e);
            throw new BizException("服务器异常，请稍后重试");
        }

        if (user.isEmpty()) {
            throw new BizException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.get().getPassword())) {
            throw new BizException(401, "密码错误");
        }
        return jwtUtil.generateToken(user.get().getId());
    }

    @Override
    @Transactional
    public UserUpdateResponseDTO updateUser(String userId, UserUpdateRequestDTO dto) {
        validateUserUpdateRequestDTO(dto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        // 如果要修改密码，则验证原密码
        if (dto.getPassword() != null) {
            if (dto.getOriginPassword() == null || !passwordEncoder.matches(dto.getOriginPassword(), user.getPassword())) {
                throw new BizException("原密码错误");
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // 验证邮箱验证码
        if (dto.getEmail() != null) {
            validateCode("email:" + dto.getEmail(), dto.getEmailcode(), "邮箱");

            // 检查邮箱是否被其他账号使用
            Optional<User> userByEmail = userRepository.findByEmail(dto.getEmail());
            if (userByEmail.isPresent() && !userByEmail.get().getId().equals(userId)) {
                throw new BizException("该邮箱已被其他账号绑定");
            }

            user.setEmail(dto.getEmail());
        }

        // 验证短信验证码
        if (dto.getPhone() != null) {
            validateCode("sms:" + dto.getPhone(), dto.getPhonecode(), "短信");
            // 检查邮箱是否被其他账号使用
            Optional<User> userByPhone = userRepository.findByPhone(dto.getPhone());
            if (userByPhone.isPresent() && !userByPhone.get().getId().equals(userId)) {
                throw new BizException("该电话已被其他账号绑定");
            }
            user.setPhone(dto.getPhone());
        }

        // 修改用户名（可选）
        if (dto.getUsername() != null) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new BizException("用户名已存在");
            }
            user.setUsername(dto.getUsername());
        }

        try {
            User updatedUser = userRepository.save(user);

            UserUpdateResponseDTO responseDTO = new UserUpdateResponseDTO();
            responseDTO.setUsername(updatedUser.getUsername());
            responseDTO.setEmail(updatedUser.getEmail());
            responseDTO.setPhone(updatedUser.getPhone());
            return responseDTO;
        } catch (DataAccessException e) {
            log.error("数据库异常", e);
            throw new BizException("更新失败，请稍后重试");
        }
    }


    private void validateCode(String key, String providedCode, String type) {
        String cachedCode = redisTemplate.opsForValue().get(key);
        if (cachedCode == null) {
            throw new BizException(type + "验证码已过期");
        }
        if (!cachedCode.equals(providedCode)) {
            throw new BizException(type + "验证码错误");
        }
    }


    @Override
    @Transactional
    public String updateUserAvatar(String userId, MultipartFile avatarFile) {
        if (!StringUtils.hasText(userId)) throw new BizException("用户ID不能为空");
        if (avatarFile == null || avatarFile.isEmpty()) throw new BizException("文件不能为空");

        User user = userRepository.findById(userId).orElseThrow(() -> new BizException("用户不存在"));
        try {
            String suffix = StringUtils.getFilenameExtension(avatarFile.getOriginalFilename());
            if (suffix == null || suffix.isEmpty()) {
                throw new BizException("文件格式错误");
            }
            String newAvatarUrl = "user/" + userId + "/" + UUID.randomUUID() + "." + suffix;
            //String newAvatarUrl = "avatar/defaults/d3.jpg";
            InputStream inputStream = avatarFile.getInputStream();
            AliyunOssUtil.upload(newAvatarUrl, inputStream);

            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                AliyunOssUtil.delete(user.getAvatarUrl());
            }

            user.setAvatarUrl(newAvatarUrl);
            user.setUpdatedTime(LocalDateTime.now());
            userRepository.save(user);

            return AliyunOssUtil.generatePresignedGetUrl(newAvatarUrl, EXPIRE_TIME);
        } catch (IOException e) {
            log.error("文件上传异常", e);
            throw new BizException("文件上传失败");
        } catch (Exception e) {
            log.error("文件上传未知异常", e);
            throw new BizException("文件上传失败，请稍后重试");
        }
    }


    @Override
    public UserInfoResponseDTO getUserInfoById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));
        String presignedUrl = null;
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            presignedUrl = AliyunOssUtil.generatePresignedGetUrl(user.getAvatarUrl(), EXPIRE_TIME);
        }
        // 手动将 User 实体类的属性赋值到 UserInfoResponseDTO
        UserInfoResponseDTO dto = new UserInfoResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAvatarUrl(presignedUrl);
        dto.setCreatedTime(user.getCreatedTime());
        dto.setUpdatedTime(user.getUpdatedTime());
        return dto;
    }


    @Override
    @Transactional
    public boolean deleteUserAndAllRelatedData(String userId) {
        if (!StringUtils.hasText(userId)) throw new BizException("用户ID不能为空");

        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new BizException("用户不存在"));
            userRepository.delete(user);
            // TODO: 删除其他关联数据
            return true;
        } catch (DataAccessException e) {
            log.error("数据库异常", e);
            throw new BizException("删除失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public void recoverPassword(RecoverPasswordRequestDTO dto) {
        User user;

        if (dto.getEmail() != null) {
            String code = redisTemplate.opsForValue().get("email:" + dto.getEmail());
            if (code == null || !code.equals(dto.getCode())) {
                throw new BizException("邮箱验证码错误或已过期");
            }
            user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new BizException("邮箱未注册"));
        } else if (dto.getPhone() != null) {
            String code = redisTemplate.opsForValue().get("sms:" + dto.getPhone());
            if (code == null || !code.equals(dto.getCode())) {
                throw new BizException("短信验证码错误或已过期");
            }
            user = userRepository.findByPhone(dto.getPhone())
                    .orElseThrow(() -> new BizException("手机号未注册"));
        } else {
            throw new BizException("邮箱或手机号必须提供一个");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword()))
            throw new BizException("新密码不能与旧密码相同");
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unbindEmail(String userId, String code) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BizException("用户不存在"));
        if (user.getEmail() == null) throw new BizException("未绑定邮箱");

        String cachedCode = redisTemplate.opsForValue().get("email:" + user.getEmail());
        if (cachedCode == null || !cachedCode.equals(code)) {
            throw new BizException("邮箱验证码错误或已过期");
        }

        user.setEmail(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unbindPhone(String userId, String code) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BizException("用户不存在"));
        if (user.getPhone() == null) throw new BizException("未绑定手机号");

        String cachedCode = redisTemplate.opsForValue().get("sms:" + user.getPhone());
        if (cachedCode == null || !cachedCode.equals(code)) {
            throw new BizException("短信验证码错误或已过期");
        }

        user.setPhone(null);
        userRepository.save(user);
    }


    //校验？

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[A-Za-z\\d]{6,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.\\w{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11}$");

    private void validateUserRegisterDTO(UserRegisterRequestDTO dto) {
        if (dto == null) throw new BizException("请求参数不能为空");

        String username = dto.getUsername();
        if (!StringUtils.hasText(username)) throw new BizException("用户名不能为空");
        if (username.length() < 1 || username.length() > 20) throw new BizException("用户名长度应为1~20位");
        if (username.matches("^\\d{11}$")) throw new BizException("用户名不能是纯11位数字");

        String password = dto.getPassword();
        if (!StringUtils.hasText(password)) throw new BizException("密码不能为空");
        if (!PASSWORD_PATTERN.matcher(password).matches()) throw new BizException("密码必须为6~20位数字和字母组合");
    }

    private void validatePhoneRegisterDTO(PhoneRegisterRequestDTO dto) {
        if (dto == null) throw new BizException("请求参数不能为空");

        String phone = dto.getPhone();
        if (!StringUtils.hasText(phone)) throw new BizException("手机号不能为空");
        if (!PHONE_PATTERN.matcher(phone).matches()) throw new BizException("手机号格式错误");

        String password = dto.getPassword();
        if (!StringUtils.hasText(password)) throw new BizException("密码不能为空");
        if (!PASSWORD_PATTERN.matcher(password).matches()) throw new BizException("密码必须为6~20位数字和字母组合");

        String code = dto.getCode();
        if (!StringUtils.hasText(code)) throw new BizException("验证码不能为空");
    }

    private void validateEmailRegisterDTO(EmailRegisterRequestDTO dto) {
        if (dto == null) throw new BizException("请求参数不能为空");

        String email = dto.getEmail();
        if (!StringUtils.hasText(email)) throw new BizException("邮箱不能为空");
        if (!EMAIL_PATTERN.matcher(email).matches()) throw new BizException("邮箱格式错误");

        String password = dto.getPassword();
        if (!StringUtils.hasText(password)) throw new BizException("密码不能为空");
        if (!PASSWORD_PATTERN.matcher(password).matches()) throw new BizException("密码必须为6~20位数字和字母组合");

        String code = dto.getCode();
        if (!StringUtils.hasText(code)) throw new BizException("验证码不能为空");
    }

    private void validateUserUpdateRequestDTO(UserUpdateRequestDTO dto) {
        if (dto == null) throw new BizException("请求参数不能为空");

        if (dto.getUsername() != null) {
            String username = dto.getUsername().trim();
            if (username.isEmpty()) throw new BizException("用户名不能为空");
            if (username.length() < 1 || username.length() > 20) throw new BizException("用户名长度应为1~20位");
            if (username.matches("^\\d{11}$")) throw new BizException("用户名不能是纯11位数字");
        }

        if (dto.getEmail() != null) {
            String email = dto.getEmail().trim();
            if (email.isEmpty()) throw new BizException("邮箱不能为空");
            if (!EMAIL_PATTERN.matcher(email).matches()) throw new BizException("邮箱格式错误");
        }

        if (dto.getPhone() != null) {
            String phone = dto.getPhone().trim();
            if (phone.isEmpty()) throw new BizException("手机号不能为空");
            if (!PHONE_PATTERN.matcher(phone).matches()) throw new BizException("手机号格式错误");
        }

        if (dto.getPassword() != null) {
            if (!StringUtils.hasText(dto.getOriginPassword())) {
                throw new BizException("修改密码必须提供原密码");
            }
            if (!PASSWORD_PATTERN.matcher(dto.getPassword()).matches()) {
                throw new BizException("新密码必须为6~20位数字和字母组合");
            }
        }
    }

}
