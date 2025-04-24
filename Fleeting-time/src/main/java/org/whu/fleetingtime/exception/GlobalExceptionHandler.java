package org.whu.fleetingtime.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.whu.fleetingtime.pojo.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<Object> handleRuntime(RuntimeException e) {
        return Result.failure(401, "Invalid token");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Object> handleIllegalArgument(IllegalArgumentException e) {
        return Result.failure(400, "invalid parameters");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public Result<Object> handleExpiredJwt(ExpiredJwtException e) {
        return Result.failure(401, "expired token");
    }

    @ExceptionHandler(JwtException.class)
    public Result<Object> handleJwt(JwtException e) {
        return Result.failure(401, "invalid token");
    }

    @ExceptionHandler(BizException.class)
    public Result<Object> handleBiz(BizException e) {
        return Result.failure(e.getCode(), e.getMsg());
    }
}
