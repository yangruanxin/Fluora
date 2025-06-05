package org.whu.fleetingtime.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.whu.fleetingtime.interceptor.JwtInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/login",
                                    "/api/user/register*",
                                    "/api/sms/**",
                                    "/api/user/recover-password",// 放行
                                    "/api/map/**");// 放行
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 应用到所有 /api/ 路径下的请求
                .allowedOrigins("http://localhost:8080","http://localhost:8081","https://www.fleeting-time.xyz") // 前端应用的源地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 是否允许发送Cookie
                .maxAge(3600); // 预检请求（OPTIONS请求）的缓存时间，单位秒
    }
}

