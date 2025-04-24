package org.example.fleetingtime.exception;

import lombok.Getter;

public enum BizExceptionEnum {
    //====用户模块====
    //用户名已存在
    USER_EXIST(10001, "用户名已存在"),
    //用户名或密码错误
    USER_PASSWORD_OR_USERNAME_ERROR(10002, "用户名或密码错误"),
    //用户名不存在
    USER_NOT_EXIST(10003, "用户名不存在");


    @Getter
    private Integer code;
    @Getter
    private String msg;
    private BizExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
