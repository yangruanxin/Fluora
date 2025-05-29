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
    //文件为空
    NULL_FILE(10008, "file cannot be null"),

    //====travelPost模块
    //非法的记录参数
    INVALID_POST_PARAMETER(10100, "latitude, longitude, city or timestamp is null"),
    //order数组有误
    INVALID_ORDER_ARRAY(10101, "Picture count and order array length mismatch"),
    //传图片失败
    IMAGE_UPLOAD_FAILED(10102, "image upload failed"),
    //post不存在
    POST_NOT_FOUND(10103, "post not found"),
    //操作非自己的post
    UNAUTHORIZED_OPERATION(10104, "unauthorized operation"),
    //数据库错误
    DATABASE_ERROR(10105, "database error"),

    //====map模块
    // 请求超时
    REQUEST_TIMEOUT(10200,"request baidu api timeout"),
    // 未知其他错误
    API_ERROR(10201, "api error");


    private final Integer code;
    private final String msg;
    BizExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
