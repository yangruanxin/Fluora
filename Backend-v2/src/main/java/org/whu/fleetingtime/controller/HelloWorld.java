package org.whu.fleetingtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/hello-world")
@Tag(name = "测试接口", description = "用来进行hello world测试")
public class HelloWorld {
    @GetMapping("hello")
    @Operation(summary = "Hello World", description = "你好世界。")
     public String hello() {
        return "Hello World!";
    }
}
