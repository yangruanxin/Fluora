package org.whu.fleetingtime.interceptor;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    // 前置拦截方法
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 获取请求头中的 token
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setContentType("text/html;charset = UTF-8");
            response.setStatus(401);  // 未授权
            response.getWriter().write("未提供Token");
            return false;
        }
        return true;
    }
}
