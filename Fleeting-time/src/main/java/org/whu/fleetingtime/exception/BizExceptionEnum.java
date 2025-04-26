package org.whu.fleetingtime.exception;

import lombok.Getter;

@Getter
public enum BizExceptionEnum {

    //====用户模块====
    //没找到对应id用户
    USER_NOT_FOUND(10001,"user not found"),
    //登录时候用户名或密码错误
    USER_PASSWORD_OR_USERNAME_ERROR(10002, "invalid username or password"),
    //修改密码时输入原始密码错误
    PASSWORD_INCORRECT(10003, "password incorrect"),
    //用户名已存在
    USERNAME_ALREADY_EXISTS(10004, "username already exists"),
    //传文件失败
    FILE_UPLOAD_FAILED(10005, "file upload failed" ),
    //修改参数全为空
    ALL_NULL_PARAMETERS(10006, "parameters cannot all be null"),
    //新旧密码不能一样
    SAME_PASSWORD(10007, "old and new passwords cannot be the same"),

    //====checkin模块
    //非法的打卡参数
    INVALID_CHECKIN_PARAMETER(10100, "latitude, longitude, city or timestamp is null");


    private final Integer code;
    private final String msg;
    BizExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
