package org.example.fleetingtime.advice;

import org.example.fleetingtime.common.R;
import org.example.fleetingtime.exception.BizException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//全局异常处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    public R error(Exception e) {
        System.out.println(e.getMessage());
        return R.error(500, e.getMessage());
    }
    public R hanleBizException(BizException e) {
        String msg = e.getMessage();
        System.out.println(msg);
        return R.error(500, msg);
    }

}
