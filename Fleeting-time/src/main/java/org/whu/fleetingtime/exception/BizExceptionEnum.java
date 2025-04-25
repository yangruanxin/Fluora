package org.whu.fleetingtime.exception;

import lombok.Getter;

public enum BizExceptionEnum {

    //====用户模块====
    //用户名或密码错误
    USER_PASSWORD_OR_USERNAME_ERROR(10002, "invalid username or password"),
    //用户名已存在
    USERNAME_ALREADY_EXISTS(10003, "username already exists"),

    //====checkin模块
    //用户名已存在
    INVALID_CHECKIN_PARAMETER(10004, "latitude, longitude, city or timestamp is null");


    @Getter
    private Integer code;
    @Getter
    private String msg;
    private BizExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
