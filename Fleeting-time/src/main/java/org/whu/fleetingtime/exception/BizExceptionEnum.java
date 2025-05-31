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
    API_ERROR(10201, "api error"),

    //===sms模块
    //发送失败
    SEND_FAILED(10300, "sms send failed"),
    //验证码错误
    CODE_ERROR(10301, "sms code error"),
    //验证码过期
    CODE_EXPIRED(10302, "code expired"),
    //验证码发送过于频繁
    CODE_TOO_FREQUENTLY(10303, "sms code too frequently"),
    //手机号已注册
    PHONE_ALREADY_EXISTS(10304, "phone already exists"),
    //手机号格式错误
    PHONE_FORMAT_ERROR(10305, "phone format error"),
    //邮箱已存在
    EMAIL_ALREADY_EXISTS(10306, "email already exists"),
    //邮箱格式错误
    EMAIL_FORMAT_ERROR(10307, "email format error"),
    //注册失败
    REGISTER_FAILED(10308, "register failed"),
    //密码错误
    PASSWORD_ERROR(10309, "password error"),

    //===friend模块
    //好友不存在
    FRIEND_NOT_FOUND(10400, "friend not found"),
    //好友已存在
    FRIEND_ALREADY_EXISTS(10401, "friend already exists"),
    //好友请求已发送
    FRIEND_REQUEST_ALREADY_SENT(10402, "friend request already sent"),
    //好友请求不存在
    FRIEND_REQUEST_NOT_FOUND(10403, "friend request not found"),
    //好友请求已处理
    FRIEND_REQUEST_ALREADY_HANDLED(10404, "friend request already handled"),
    //好友请求已过期
    FRIEND_REQUEST_EXPIRED(10405, "friend request expired"),
    //好友请求已拒绝
    FRIEND_REQUEST_REJECTED(10406, "friend request rejected"),
    //好友请求已接受
    FRIEND_REQUEST_ACCEPTED(10407, "friend request accepted"),
    //好友请求已删除
    FRIEND_REQUEST_DELETED(10408, "friend request deleted"),
    //好友请求已取消
    FRIEND_REQUEST_CANCELED(10409, "friend request canceled"),
    ;


    private final Integer code;
    private final String msg;
    BizExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
