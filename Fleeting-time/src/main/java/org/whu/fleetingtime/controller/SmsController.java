package org.whu.fleetingtime.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.dto.user.CaptchaValidationDTO;
import org.whu.fleetingtime.service.SmsService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    @GetMapping("/smssend")
    public Result<String> send(@RequestParam String phone) {
        logger.info("【短信发送请求】手机号：{}", phone);
        boolean result = smsService.sendSms(phone);
        return result ? Result.success("短信发送成功!")
                : Result.failure("短信发送失败！");
    }
    @PostMapping("/emailsend")
    public Result<String> sendCode(@RequestParam String email) {
        logger.info("【邮件发送请求】邮箱：{}", email);
        boolean result = smsService.sendEmail(email);
        return result ? Result.success("邮件发送成功!")
                : Result.failure("邮件发送失败！");
    }
    @GetMapping("/captchasend")
    public Result<Map<String, String>> getCaptcha(){
        String uuid = UUID.randomUUID().toString();
        BufferedImage image = smsService.sendCaptcha(uuid);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Img = Base64.getEncoder().encodeToString(imageBytes);

            Map<String, String> data = new HashMap<>();
            data.put("uuid", uuid);
            data.put("img", "data:image/jpeg;base64," + base64Img);

            return Result.success(data);
        } catch (IOException e) {
            return Result.failure("生成验证码失败");
        }
    }
    @PostMapping("/validatecaptcha")
    public Result<String> validateCaptcha(@RequestBody CaptchaValidationDTO dto) {
        boolean result = smsService.validateCaptcha(dto.getUuid(), dto.getCode());
        return result ? Result.success("验证码正确!")
                : Result.failure("验证码错误！");
    }
}

