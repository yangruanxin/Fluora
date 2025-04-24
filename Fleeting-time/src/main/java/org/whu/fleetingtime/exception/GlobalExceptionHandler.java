package org.whu.fleetingtime.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> handleRuntime(RuntimeException e) {
        return buildError(500, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException e) {
        return buildError(400, "非法请求参数：" + e.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public Map<String, Object> handleExpiredJwt(ExpiredJwtException e) {
        return buildError(401, "Token 已过期，请重新登录。" + e.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public Map<String, Object> handleJwt(JwtException e) {
        return buildError(401, "无效的Token：" + e.getMessage());
    }

    private Map<String, Object> buildError(int code, String msg) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", code);
        res.put("msg", msg);
        return res;
    }
}
