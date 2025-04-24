package org.example.fleetingtime.exception;

public class BizException extends RuntimeException{
    private Integer code;
    private String msg;
    public BizException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    public BizException(BizExceptionEnum bizExceptionEnum) {
        super(bizExceptionEnum.getMsg());
        this.code = bizExceptionEnum.getCode();
        this.msg = bizExceptionEnum.getMsg();
    }

}
