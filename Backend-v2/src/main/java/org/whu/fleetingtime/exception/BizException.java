package org.whu.fleetingtime.exception; // 建议放在exception包下

import lombok.Getter;

@Getter
public class BizException extends RuntimeException { // 继承RuntimeException，变为非受检异常

    private final Integer code; // 业务错误码
    private final String msg; // 父类的message字段已经存储了消息

    public BizException(Integer code, String message) {
        super(message); // 将消息传递给父类的构造函数
        this.code = code;
        this.msg = message;
    }

    public BizException(String message) {
        super(message); // 将消息传递给父类的构造函数
        this.code = 400;
        this.msg = message;
    }

    // 如果将来还是想用枚举，这个构造函数很好
    // public BizException(BizExceptionEnumInterface bizExceptionEnum) {
    //     super(bizExceptionEnum.getMessage());
    //     this.code = bizExceptionEnum.getCode();
    //     // this.msg = bizExceptionEnum.getMessage();
    // }

    // 这个构造函数允许用枚举的code，但自定义message，也很有用
    // public BizException(BizExceptionEnumInterface bizExceptionEnum, String customMessage) {
    //     super(customMessage); // 使用自定义消息
    //     this.code = bizExceptionEnum.getCode();
    //     // this.msg = customMessage;
    // }
}
