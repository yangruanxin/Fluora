package org.whu.fleetingtime.config;

import org.whu.fleetingtime.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置拦截路径，这里设置拦截所有 /api 开头的请求
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**") // 要拦截的路径
                .excludePathPatterns("/api/user/login", "/api/user/register", "/api/map"); // 不拦截登录和注册接口

    }
}
