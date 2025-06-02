package org.whu.fleetingtime.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.dto.sms.CaptchaValidationDTO;
import org.whu.fleetingtime.service.SmsService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("api/sms")
@Tag(name = "验证码接口", description = "")
public class SmsController {
    @Autowired
    private SmsService smsService;

    // 发送短信验证码
    @GetMapping("/smssend")
    public Result<String> sendSms(@RequestParam String phone) {
        log.info("【短信验证码请求】手机号：{}", phone);
        boolean result = smsService.sendSms(phone);
        return result ? Result.success("短信发送成功!")
                : Result.failure("短信发送失败！");
    }

    // 发送邮箱验证码
    @PostMapping("/emailsend")
    public Result<String> sendEmail(@RequestParam String email) {
        log.info("【邮箱验证码请求】邮箱：{}", email);
        boolean result = smsService.sendEmail(email);
        return result ? Result.success("邮件发送成功!")
                : Result.failure("邮件发送失败！");
    }

    // 获取图形验证码
    @GetMapping("/captchasend")
    public Result<Map<String, String>> sendCaptcha() {
        String uuid = UUID.randomUUID().toString();

        BufferedImage image = smsService.sendCaptcha(uuid);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Img = Base64.getEncoder().encodeToString(imageBytes);

            Map<String, String> data = new HashMap<>();
            data.put("uuid", uuid);
            data.put("img", "data:image/jpeg;base64," + base64Img);

            log.info("【图形验证码生成成功】uuid: {}", uuid);
            return Result.success(data);
        } catch (IOException e) {
            log.error("【图形验证码生成失败】", e);
            return Result.failure("生成验证码失败");
        }
    }

    // 校验图形验证码
    @PostMapping("/validatecaptcha")
    public Result<String> validateCaptcha(@RequestBody CaptchaValidationDTO dto) {
        log.info("【图形验证码校验】uuid: {}", dto.getUuid());
        boolean result = smsService.validateCaptcha(dto.getUuid(), dto.getCode());
        return result ? Result.success("验证码正确!")
                : Result.failure("验证码错误！");
    }
}
