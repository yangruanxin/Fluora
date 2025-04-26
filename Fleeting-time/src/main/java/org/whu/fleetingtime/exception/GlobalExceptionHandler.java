package org.whu.fleetingtime.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.whu.fleetingtime.pojo.Result;

import java.util.HashMap;
import java.util.Map;

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
    @ExceptionHandler(Throwable.class)
    public Result<Object> handleThrowable(Throwable e) {
        return Result.failure(500, "internal error");
    }
    //处理参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> errormap = new HashMap<>();
        for(FieldError fieldError : bindingResult.getFieldErrors())//拿到所有属性错误
        {
            //1.属性名
            String field = fieldError.getField();
            //2.错误信息
            String message = fieldError.getDefaultMessage();
            //System.out.println(fieldError.getField()+":"+fieldError.getDefaultMessage());
            errormap.put(field,message);
        }
        return Result.failure(400, "Invalid Data",errormap);
    }
}
