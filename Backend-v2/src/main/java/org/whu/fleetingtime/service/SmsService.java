package org.whu.fleetingtime.service;

import java.awt.image.BufferedImage;

public interface SmsService {
    boolean sendSms(String mobile);
    boolean sendEmail(String email);
    BufferedImage sendCaptcha(String uuid, String identifier);
    boolean validateCaptcha(String uuid, String captcha);
}
