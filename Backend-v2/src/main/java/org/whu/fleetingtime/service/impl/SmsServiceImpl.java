package org.whu.fleetingtime.service.impl;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.whu.fleetingtime.entity.User;
import org.whu.fleetingtime.exception.BizException;
import org.whu.fleetingtime.repository.UserRepository;
import org.whu.fleetingtime.service.SmsService;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private DefaultKaptcha captchaProducer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Value("${sms.host}")
    private String host;

    @Value("${sms.path}")
    private String path;

    @Value("${sms.method}")
    private String method;

    @Value("${sms.appcode}")
    private String appcode;

    @Value("${sms.smsSignId}")
    private String smsSignId;

    @Value("${sms.templateId}")
    private String templateId;

    @Value("${spring.mail.username}")
    private String from;

//    @Override
//    public boolean sendSms(String mobile) {
//        if (!StringUtils.hasText(mobile)) {
//            throw new BizException("手机号不能为空");
//        }
//        if (!mobile.matches("^\\d{11}$")) {
//            throw new BizException("手机号格式不正确");
//        }
//        // 防止频繁发送（1分钟限制）
//        String rateLimitKey = "sms:rate:" + mobile;
//        if (Boolean.TRUE.equals(redisTemplate.hasKey(rateLimitKey))) {
//            throw new BizException("验证码发送过于频繁，请稍后再试");
//        }
//
//        int minutes=5;
//        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000)); // 生成6位验证码
//        String url = host + path +
//                "?mobile=" + mobile +
//                "&param=" + "**code**:" + code + ",**minute**:" + minutes +
//                "&smsSignId=" + smsSignId +
//                "&templateId=" + templateId;
//        //设置请求头
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "APPCODE " + appcode);
//        //创建请求实体
//        HttpEntity<String> entity = new HttpEntity<>(null, headers);
//        //发送 HTTP 请求
//        try {
//            ResponseEntity<String> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.POST,
//                    entity,
//                    String.class
//            );
//            System.out.println("响应状态码：" + response.getStatusCode());
//            System.out.println("响应内容：" + response.getBody());
//            //存入redis
//            if (response.getStatusCode().is2xxSuccessful()) {
//                // 限制1分钟重复发送
//                redisTemplate.opsForValue().set(rateLimitKey, "1", 60, TimeUnit.SECONDS);
//                // 存验证码，有效期5分钟
//                redisTemplate.opsForValue().set("sms:" + mobile, code, 5, TimeUnit.MINUTES);
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    @Override
    public boolean sendSms(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            throw new BizException("手机号不能为空");
        }
        if (!mobile.matches("^\\d{11}$")) {
            throw new BizException("手机号格式不正确");
        }

        String rateLimitKey = "sms:rate:" + mobile;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(rateLimitKey))) {
            throw new BizException("验证码发送过于频繁，请稍后再试");
        }

        int minutes = 5;
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000)); // 6位验证码

        String url = host + path +
                "?mobile=" + mobile +
                "&param=" + "**code**:" + code + ",**minute**:" + minutes +
                "&smsSignId=" + smsSignId +
                "&templateId=" + templateId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "APPCODE " + appcode);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BizException("短信发送失败，请稍后再试");
        }

        // 设置验证码及频率限制
        redisTemplate.opsForValue().set(rateLimitKey, "1", 60, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set("sms:" + mobile, code, 5, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public boolean sendEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BizException("邮箱不能为空");
        }
        if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.\\w{2,}$")) {
            throw new BizException("邮箱格式不正确");
        }
        // 防止频繁发送（1分钟限制）
        String rateLimitKey = "email:rate:" + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(rateLimitKey))) {
            throw new BizException("验证码发送过于频繁，请稍后再试");
        }
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000)); // 生成6位验证码
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("【注册验证码】");

            String htmlContent = """
                    <div style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                        <div style="max-width: 500px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                            <div style="text-align: center;">
                                <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/4/4a/Logo_2013_Google.png/320px-Logo_2013_Google.png" width="120" style="margin-bottom: 20px;" alt="Logo"/>
                            </div>
                            <h2 style="color: #333;text-align: center">欢迎注册！</h2>
                            <p style="font-size: 16px; color: #555;text-align: center">
                                您的验证码是：
                                <span style="color: red; font-weight: bold; font-size: 20px;">%s</span>
                            </p>
                            <p style="font-size: 14px; color: #666;text-align: center">此验证码将在 <strong>5 分钟</strong> 后失效，请及时使用。</p>
                            <div style="text-align: center; margin: 30px 0;">
                                <a href="https://your-website.com/register" style="background-color: #4CAF50; color: white; padding: 12px 24px; border-radius: 5px; text-decoration: none; font-weight: bold;">
                                    立即注册
                                </a>
                            </div>
                            <hr style="border: none; border-top: 1px solid #eee;text-align: center" />
                            <p style="font-size: 12px; color: #999;">如果您并未请求验证码，请忽略此邮件。</p>
                        </div>
                    </div>
                    """.formatted(code); //%S占位符，图片和跳转链接

            helper.setText(htmlContent, true);
            mailSender.send(message);
            // 存入 Redis，设置 1 分钟内不允许重复发送
            redisTemplate.opsForValue().set(rateLimitKey, "1", 60, TimeUnit.SECONDS);
            // 存入 Redis
            redisTemplate.opsForValue().set("email:" + email, code, 5, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("邮件发送失败，请稍后再试");
        }
    }
    public BufferedImage sendCaptcha(String uuid,String identifier) {
        if (!StringUtils.hasText(uuid)) {
            throw new BizException("验证码标识不能为空");
        }
        Optional<User> user = Optional.empty();
        if (identifier.matches("^\\d{11}$")) {
            user = userRepository.findByPhone(identifier);

        } else if (identifier.matches("^[\\w.%+-]+@[\\w.-]+\\.\\w{2,}$")) {
            user = userRepository.findByEmail(identifier);
        } else {
            user = Optional.ofNullable(userRepository.findByUsername(identifier));
        }
        //已被注册
        if (user.isPresent()) {
            throw new BizException("该账号已被注册");
        }
        String code = captchaProducer.createText();
        BufferedImage image = captchaProducer.createImage(code);
        redisTemplate.opsForValue().set("captcha:" + uuid, code, 5, TimeUnit.MINUTES);
        return image;
    }
    public boolean validateCaptcha(String uuid, String inputCode) {
        if (inputCode == null) {
            throw new BizException("输入为空"); // 参数为空也视为错误
        }
        if (uuid == null) {
            throw new BizException("验证码标识丢失"); // 参数为空也视为错误
        }

        String redisKey = "captcha:" + uuid;
        String cachedCode = redisTemplate.opsForValue().get(redisKey);

        if (cachedCode == null) {
            throw new BizException("验证码失效");
        }

        if (!cachedCode.equalsIgnoreCase(inputCode)) {
            throw new BizException("验证码错误");
        }

        redisTemplate.delete(redisKey); // 使用一次即失效
        return true;
    }

}
