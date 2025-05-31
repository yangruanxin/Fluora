package org.whu.fleetingtime.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {
        @Bean
        public DefaultKaptcha producer() {
            DefaultKaptcha kaptcha = new DefaultKaptcha();
            Properties properties = new Properties();
            properties.setProperty("kaptcha.border", "no");
            properties.setProperty("kaptcha.textproducer.font.color", "blue");
            properties.setProperty("kaptcha.image.width", "120");
            properties.setProperty("kaptcha.image.height", "40");
            properties.setProperty("kaptcha.textproducer.font.size", "32");
            properties.setProperty("kaptcha.session.key", "code");
            properties.setProperty("kaptcha.textproducer.char.length", "5");
            properties.setProperty("kaptcha.textproducer.char.string", "abcde2345678gfynmnpwx");
            properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
            Config config = new Config(properties);
            kaptcha.setConfig(config);
            return kaptcha;
        }
}


