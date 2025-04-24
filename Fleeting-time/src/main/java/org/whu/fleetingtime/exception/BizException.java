package org.whu.fleetingtime.exception;

public class BizException extends RuntimeException {
    private Integer code;
    private String msg;
    public BizException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    public BizException(BizExceptionEnum bizExceptionenum) {
        super(bizExceptionenum.getMsg());
        this.code = bizExceptionenum.getCode();
        this.msg = bizExceptionenum.getMsg();
    }
}
