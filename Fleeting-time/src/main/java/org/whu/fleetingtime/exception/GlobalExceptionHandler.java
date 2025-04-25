package org.whu.fleetingtime.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.whu.fleetingtime.pojo.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public Result<Object> handleRuntime(RuntimeException e) {
        logger.error("运行时异常：{}", e.getMessage(), e);
        return Result.failure(500, "internal server error");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Object> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("参数异常：{}", e.getMessage());
        return Result.failure(400, "invalid parameters");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public Result<Object> handleExpiredJwt(ExpiredJwtException e) {
        logger.warn("JWT已过期：{}", e.getMessage());
        return Result.failure(401, "expired token");
    }

    @ExceptionHandler(JwtException.class)
    public Result<Object> handleJwt(JwtException e) {
        logger.warn("无效的JWT：{}", e.getMessage());
        return Result.failure(401, "invalid token");
    }

    @ExceptionHandler(BizException.class)
    public Result<Object> handleBiz(BizException e) {
        logger.info("业务异常，代码: {}，消息: {}", e.getCode(), e.getMsg());
        return Result.failure(e.getCode(), e.getMsg());
    }
}
