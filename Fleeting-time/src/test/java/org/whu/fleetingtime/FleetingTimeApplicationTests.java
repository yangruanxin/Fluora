package org.whu.fleetingtime;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
class FleetingTimeApplicationTests {
    @Autowired
    private ApplicationContext context;
    @Test
    void FleetingTimeApplicationTest() {
        System.out.println("JavaMailSender bean = " + context.getBean(JavaMailSender.class));
    }
}
