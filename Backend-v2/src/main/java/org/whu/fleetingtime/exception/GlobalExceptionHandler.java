package org.whu.fleetingtime.exception;

//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.JwtException; // MalformedJwtException, SignatureException 等都继承自它
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus; // 用于 @ResponseStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // 用于处理 @Valid 校验失败
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.whu.fleetingtime.common.Result; // 你的统一返回类

import java.time.format.DateTimeParseException;

@RestControllerAdvice // @ControllerAdvice + @ResponseBody，返回JSON
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理自定义的业务异常 BizException
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 业务异常通常返回HTTP 200，具体错误通过Result的code和message体现
    public Result<?> handleBizException(BizException e) {
        // 对于业务异常，通常不需要打印完整的堆栈，除非错误码指示了严重问题或用于调试
        logger.warn("业务异常发生: [Code: {}, Message: {}]", e.getCode(), e.getMessage());
        return Result.failure(e.getCode(), e.getMessage());
    }

    // --- 新增：专门处理 NoResourceFoundException (404 Not Found) ---
    @ExceptionHandler(NoResourceFoundException.class)
    // @ResponseStatus(HttpStatus.NOT_FOUND) // 如果用ResponseEntity，这个可以省略
    public Result<?> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        String requestUrl = request.getRequestURI();
        // 对于404错误，通常记录为WARN级别，并包含请求的URL和方法，一般不需要完整堆栈
        logger.warn("请求的资源未找到 (404): {} {} (Referer: {})",
                request.getMethod(),
                requestUrl,
                request.getHeader("Referer")); // 记录访问来源，有助于分析
        return Result.failure(HttpStatus.NOT_FOUND.value(), "您访问的页面或资源不存在");
    }

    // 处理 @Valid 注解参数校验失败的异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 参数校验失败，返回HTTP 400
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 从异常中获取第一个校验错误信息作为提示
        String firstErrorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        logger.warn("请求参数校验失败: {}", firstErrorMessage, e); // 记录更详细的异常信息用于调试
        return Result.failure(HttpStatus.BAD_REQUEST.value(), "参数校验失败");
    }

    // 处理其他常见的运行时异常 (可以根据需要细化)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("非法参数异常: {}", e.getMessage(), e);
        return Result.failure(HttpStatus.BAD_REQUEST.value(), "请求参数不合法: " + e.getMessage());
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleDateTimeParseException(DateTimeParseException e) {
        logger.warn("日期时间格式解析错误: {}", e.getMessage(), e);
        return Result.failure(HttpStatus.BAD_REQUEST.value(), "日期时间格式无效，期望格式：yyyy-MM-dd HH:mm:ss");
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleNumberFormatException(NumberFormatException e) {
        logger.warn("数字格式转换错误: {}", e.getMessage(), e);
        return Result.failure(HttpStatus.BAD_REQUEST.value(), "提供的数字格式无效");
    }

    // --- JWT 相关异常处理 ---
//    @ExceptionHandler(ExpiredJwtException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED) // Token过期，返回HTTP 401
//    public Result<?> handleExpiredJwtException(ExpiredJwtException e) {
//        logger.warn("JWT已过期: {}", e.getMessage()); // 可以不打印完整堆栈，因为这是预期内的客户端错误
//        return Result.failure(HttpStatus.UNAUTHORIZED.value(), "登录已过期，请重新登录");
//    }
//
//    @ExceptionHandler(JwtException.class) // 捕获其他JWT相关异常，如签名错误、格式错误等
//    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 无效Token，返回HTTP 401
//    public Result<?> handleJwtException(JwtException e) {
//        logger.warn("无效的JWT: {}", e.getMessage());
//        return Result.failure(HttpStatus.UNAUTHORIZED.value(), "无效的身份认证信息");
//    }

    // --- 通用运行时异常处理器 (作为最后的防线) ---
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 未知运行时异常，返回HTTP 500
    public Result<?> handleRuntimeException(RuntimeException e) {
        logger.error("未捕获的运行时异常，系统内部错误: ", e); // 记录完整堆栈，方便排查
        return Result.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器繁忙，请稍后再试");
    }

    // 如果需要，可以添加处理 Exception.class 的方法，但通常 RuntimeException 已经能覆盖大部分情况
     @ExceptionHandler(Exception.class)
     @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
     public Result<?> handleException(Exception e) {
         logger.error("未处理的顶层异常: ", e);
         return Result.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器繁忙，请稍后再试");
     }
}
