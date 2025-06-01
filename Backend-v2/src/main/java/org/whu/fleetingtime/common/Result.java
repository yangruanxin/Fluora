package org.whu.fleetingtime.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功返回
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    // 成功返回
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    // 自定义成功信息
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // 失败返回
    public static <T> Result<T> failure(String message) {
        return new Result<>(400, message, null);
    }

    public static <T> Result<T> failure(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> failure(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }
}
