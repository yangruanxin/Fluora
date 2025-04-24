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
        System.out.println(e.getMessage());
        return buildError(400, "Illegal request parameters: ");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public Map<String, Object> handleExpiredJwt(ExpiredJwtException e) {
        System.out.println(e.getMessage());
        return buildError(401, "The Token has expired, Please log in again");
    }

    @ExceptionHandler(JwtException.class)
    public Map<String, Object> handleJwt(JwtException e) {
        System.out.println(e.getMessage());
        return buildError(401, "Invalid Token");
    }

    private Map<String, Object> buildError(int code, String msg) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", code);
        res.put("msg", msg);
        res.put("data","failure");
        return res;
    }
}
