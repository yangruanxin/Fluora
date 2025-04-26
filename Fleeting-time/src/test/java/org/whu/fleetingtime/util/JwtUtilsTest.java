package org.whu.fleetingtime.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilsTest {

    private final String secretKey="YourSuperSecretKeyForJwtMustBeLongEnough123!";

    @Test
    public void testCreateAndParseJwt() {
        // 创建claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 2);

        // 创建token
        String token = JwtUtils.createJwt(secretKey,3600000, claims); // 1小时有效期
        assertNotNull(token);
        System.out.println("生成的token: " + token);

        // 解析token
        Claims parsedClaims = JwtUtils.parseJWT(secretKey,token);
        assertEquals(2, parsedClaims.get("id"));

        System.out.println("解析成功: " + parsedClaims);
    }

    @Test
    public void testTokenExpired() throws InterruptedException {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "expired");

        String token = JwtUtils.createJwt(secretKey,1000, claims); // 1秒
        Thread.sleep(1500); // 等待超过过期时间

        try {
            JwtUtils.parseJWT(secretKey,token);
            fail("应当抛出 ExpiredJwtException");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("成功捕获过期异常：" + e.getMessage());
        }
    }
}
